from pydantic import BaseModel

class UserCreate(BaseModel):
    uid: str
    secret: str

class UserOut(BaseModel):
    uid: str
    blocked: bool
