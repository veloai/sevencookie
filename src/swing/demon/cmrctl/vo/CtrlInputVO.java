package swing.demon.cmrctl.vo;

public class CtrlInputVO {
    private static String RESULT_FORMAT = "%8s^%9s^%2s^%3s^%1s^%1s^%14s^%14s^";

    private String reqSeq;
    private String cmrId;
    private String dvcId;
    private String status;
    private String applyDt;
    private String regDt;

    public CtrlInputVO(String line) {
        int idx = 0;
        String[] param = line.split("\\^");
        this.reqSeq = param[idx++];
        this.dvcId = param[idx++];
        this.status = param[idx++];
        this.applyDt = param[idx++];
        this.regDt = param[idx++];
    }

    public static String getResultFormat() {
        return RESULT_FORMAT;
    }

    public static void setResultFormat(String resultFormat) {
        RESULT_FORMAT = resultFormat;
    }

    public String getReqSeq() {
        return reqSeq;
    }

    public void setReqSeq(String reqSeq) {
        this.reqSeq = reqSeq;
    }

    public String getCmrId() {
        return cmrId;
    }

    public void setCmrId(String cmrId) {
        this.cmrId = cmrId;
    }

    public String getDvcId() {
        return dvcId;
    }

    public void setDvcId(String dvcId) {
        this.dvcId = dvcId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplyDt() {
        return applyDt;
    }

    public void setApplyDt(String applyDt) {
        this.applyDt = applyDt;
    }

    public String getRegDt() {
        return regDt;
    }

    public void setRegDt(String regDt) {
        this.regDt = regDt;
    }

    @Override
    public String toString() {
        return "CtrlInputVO{" +
                "reqSeq='" + reqSeq + '\'' +
                ", dvcId='" + dvcId + '\'' +
                ", status='" + status + '\'' +
                ", applyDt='" + applyDt + '\'' +
                ", regDt='" + regDt + '\'' +
                '}';
    }
}
