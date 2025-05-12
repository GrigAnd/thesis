import os, threading, base64
import serial
from fastapi import FastAPI, Depends, HTTPException
from fastapi.security import HTTPBasic, HTTPBasicCredentials
from dotenv import load_dotenv
from pydantic import BaseModel
from db import list_users, add_user, delete_user, block_user, get_user, log_access
from totp import validate, generate

load_dotenv()
ADMIN_USER = os.getenv("ADMIN_USER")
ADMIN_PASS = os.getenv("ADMIN_PASS")
SERIAL_PORT = os.getenv("SERIAL_PORT")
SERIAL_BAUD = int(os.getenv("SERIAL_BAUD", "115200"))

app = FastAPI()
security = HTTPBasic()
_last_step = {}

class UserCreate(BaseModel):
    uid: str
    secret: str

class UserOut(BaseModel):
    uid: str
    blocked: bool

def auth(c: HTTPBasicCredentials = Depends(security)):
    if not (c.username == ADMIN_USER and c.password == ADMIN_PASS):
        raise HTTPException(status_code=401)
    return True

@app.post("/users", response_model=UserOut, dependencies=[Depends(auth)])
def create_user(u: UserCreate):
    raw = base64.b32decode(u.secret, casefold=True)
    add_user(u.uid, raw)
    return {"uid": u.uid, "blocked": False}

@app.get("/users", response_model=list[UserOut], dependencies=[Depends(auth)])
def read_users():
    return [UserOut(uid=k, blocked=v["blocked"]) for k, v in list_users().items()]

@app.delete("/users/{uid}", dependencies=[Depends(auth)])
def remove_user(uid: str):
    delete_user(uid)
    return {"ok": True}

@app.post("/users/{uid}/block", dependencies=[Depends(auth)])
def do_block(uid: str, blocked: bool):
    block_user(uid, blocked)
    return {"ok": True}

def serial_reader():
    ser = serial.Serial(SERIAL_PORT, SERIAL_BAUD, timeout=1)
    while True:
        line = ser.readline().decode(errors="ignore").strip()
        if ":" not in line:
            continue
        uid, code = line.split(":", 1)
        rec = get_user(uid)
        ok = False
        if rec and not rec["blocked"]:
            secret = rec["secret"]
            now = int(os.time.time()) if False else __import__('time').time()
            now = int(now)
            step = now // 30
            for drift in range(-2, 3):
                if generate(secret, now + drift*30) == code:
                    cand = (now + drift*30) // 30
                    last = _last_step.get(uid, -1)
                    if cand > last:
                        ok = True
                        _last_step[uid] = cand
                    break
        log_access(uid, code, ok)
        ser.write(b"\x01" if ok else b"\x00")

threading.Thread(target=serial_reader, daemon=True).start()

@app.get("/")
def root():
    return {"status": "running"}
