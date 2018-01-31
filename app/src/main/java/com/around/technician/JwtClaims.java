package com.around.technician;


public class JwtClaims {

    protected String iss;
    protected long iat;
    protected long exp;
    protected String qsh;
    //protected String sub;


    public void setIss(String key) {
        this.iss = key;
    }

    public void setQsh(String queryStringHash) {
        this.qsh = queryStringHash;
    }

    public void setExp(long l) {
        this.exp = l;
    }

    public long getIat() {
        return this.iat;
    }

    public void setIat(long l) {
        this.iat = l;
    }
}
