import time, hmac, hashlib, struct

def generate(secret, t=None):
    if t is None:
        t = int(time.time())
    counter = t // 30
    msg = struct.pack(">Q", counter)
    h = hmac.new(secret, msg, hashlib.sha256).digest()
    o = h[-1] & 0x0F
    code = (struct.unpack(">I", h[o:o+4])[0] & 0x7FFFFFFF) % 1000000
    return f"{code:06d}"

def validate(secret, code):
    now = int(time.time())
    for d in range(-2, 3):
        if generate(secret, now + d*30) == code:
            return True
    return False
