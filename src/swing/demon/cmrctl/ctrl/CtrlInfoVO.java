package swing.demon.cmrctl.ctrl;

public class CtrlInfoVO {
    private static String RESULT_FORMAT = "%8s^%9s^%2s^%3s^%1s^%1s^%14s^%14s";

    private String reqSeq;
    private String cmrId;
    private String dvcId;
    private String sido;
    private String sigungu;
    private String successYn;
    private String status;
    private String applyDt;
    private String regDt;
    private String cmd;
    private String ipAddress;
    private int port;

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

    public String getSido() {
        return sido;
    }

    public void setSido(String sido) {
        this.sido = sido;
    }

    public String getSigungu() {
        return sigungu;
    }

    public void setSigungu(String sigungu) {
        this.sigungu = sigungu;
    }

    public String getSuccessYn() {
        return successYn;
    }

    public void setSuccessYn(String successYn) {
        this.successYn = successYn;
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

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormatting() {

        return String.format( RESULT_FORMAT
                , this.reqSeq
                , this.dvcId
                , this.sido
                , this.sigungu
                , this.successYn
                , this.status
                , this.applyDt
                , this.regDt
        );
    }

    @Override
    public String toString() {
        return "CtrlInfoVO{" +
                "reqSeq='" + reqSeq + '\'' +
                ", dvcId='" + dvcId + '\'' +
                ", sido='" + sido + '\'' +
                ", sigungu='" + sigungu + '\'' +
                ", successYn='" + successYn + '\'' +
                ", status='" + status + '\'' +
                ", applyDt='" + applyDt + '\'' +
                ", regDt='" + regDt + '\'' +
                ", cmd='" + cmd + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                '}';
    }
    
    public String result() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(" Date=").append(applyDt)
    	.append(", dvcId=").append(dvcId)
    	.append(", cmd=").append(cmd)
    	.append(", successYn=").append(successYn)
    	.append(", status=").append(status)
    	.append(", ipAddress:port=").append(ipAddress+":"+port);
    	return sb.toString();
    }
}
