package com.qianmo.agentweb.sample.net;

public class LoginEntity {
    /* {
	"code": "",
	"data": {
		"body": {
			"isBind": "",
			"isNew": "",
			"signMode": 5
		},
		"expiresIn": 0,
		"refreshToken": "",
		"token": "",
		"tokenHead": ""
	},
	"flag": "",
	"message": "",
	"success": true
} */

    private String code;
    private String message;
    private DataBean data;
    private String flag;
    private boolean success;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class DataBean {
        private BodyBean body;
        private String expiresIn;
        private String refreshToken;
        private String token;
        private String tokenHead;

        public BodyBean getBody() {
            return body;
        }

        public void setBody(BodyBean body) {
            this.body = body;
        }

        public String getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(String expiresIn) {
            this.expiresIn = expiresIn;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getTokenHead() {
            return tokenHead;
        }

        public void setTokenHead(String tokenHead) {
            this.tokenHead = tokenHead;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "body=" + body +
                    ", expiresIn='" + expiresIn + '\'' +
                    ", refreshToken='" + refreshToken + '\'' +
                    ", token='" + token + '\'' +
                    ", tokenHead='" + tokenHead + '\'' +
                    '}';
        }
    }

    public static class BodyBean {
        private String isBind;
        private String isNew;
        private int signMode;

        public String getIsBind() {
            return isBind;
        }

        public void setIsBind(String isBind) {
            this.isBind = isBind;
        }

        public String getIsNew() {
            return isNew;
        }

        public void setIsNew(String isNew) {
            this.isNew = isNew;
        }

        public int getSignMode() {
            return signMode;
        }

        public void setSignMode(int signMode) {
            this.signMode = signMode;
        }

        @Override
        public String toString() {
            return "BodyBean{" +
                    "isBind='" + isBind + '\'' +
                    ", isNew='" + isNew + '\'' +
                    ", signMode=" + signMode +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LoginEntity{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", flag='" + flag + '\'' +
                ", success=" + success +
                '}';
    }
}
