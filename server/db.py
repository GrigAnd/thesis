import os, json, sqlite3
from threading import Lock
from cryptography.hazmat.primitives.ciphers.aead import AESGCM
from dotenv import load_dotenv

load_dotenv()
_MASTER_KEY = bytes.fromhex(os.getenv("MASTER_KEY"))
_lock = Lock()
_conn = sqlite3.connect("logs.db", check_same_thread=False)
_conn.execute("""CREATE TABLE IF NOT EXISTS logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    uid TEXT,
    code TEXT,
    ok INTEGER,
    ts DATETIME DEFAULT CURRENT_TIMESTAMP
)""")
_conn.commit()
USERS_FILE = "users.json"

def _load_users():
    if not os.path.exists(USERS_FILE):
        return {}
    raw = json.load(open(USERS_FILE))
    aes = AESGCM(_MASTER_KEY)
    out = {}
    for uid, rec in raw.items():
        nonce = bytes.fromhex(rec["nonce"])
        data = bytes.fromhex(rec["data"])
        out[uid] = {"secret": aes.decrypt(nonce, data, None), "blocked": rec.get("blocked", False)}
    return out

def _save_users(u):
    aes = AESGCM(_MASTER_KEY)
    out = {}
    for uid, rec in u.items():
        nonce = os.urandom(12)
        data = aes.encrypt(nonce, rec["secret"], None)
        out[uid] = {"nonce": nonce.hex(), "data": data.hex(), "blocked": rec.get("blocked", False)}
    with open(USERS_FILE, "w") as f:
        json.dump(out, f, indent=2)

def list_users():
    return _load_users()

def add_user(uid, secret):
    u = _load_users()
    u[uid] = {"secret": secret, "blocked": False}
    _save_users(u)

def delete_user(uid):
    u = _load_users()
    u.pop(uid, None)
    _save_users(u)

def block_user(uid, b):
    u = _load_users()
    if uid in u:
        u[uid]["blocked"] = b
        _save_users(u)

def get_user(uid):
    return _load_users().get(uid)

def log_access(uid, code, ok):
    with _lock:
        _conn.execute("INSERT INTO logs(uid,code,ok) VALUES (?,?,?)", (uid, code, int(ok)))
        _conn.commit()
