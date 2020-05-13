package demon.cmrctl.src.main.java.com.jkkorea.ftrnsfr.ctrl;

import java.util.HashMap;

/**
 * Class Name : DvcSts.java
 * Description  : 제어기 상태 전역상수
 * @author 강형모
 * @since 2018.07.17
 * @version 1.0.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일		수정자		수정내용
 *  -------    	--------    ---------------------------
 *  2018.07.17  강형모             최초 생성
 *
 * </pre>
 */
public class DvcSts {
	
	/**
	 * 정상
	 */
	public static final String NRML = "DVS001";
	
	/**
	 * 소프트 리셋
	 */
	public static final String SOFT_RESET = "DVS002";
	
	/**
	 * 하드 리셋
	 */
	public static final String HARD_RESET = "DVS003";
	
	/**
	 * 미수신
	 */
	public static final String NOT_RCV = "DVS004";
	
	/**
	 * 장애
	 */
	public static final String TRBL = "DVS005";
	
	public static final String CODE_GROUP = "AGV008";
	
	public static final int SD_NRML = 1;
	public static final int SD_TRBL = 2;
	
	public static  HashMap<Integer, String> STS_MPNG = new HashMap<Integer, String>() {{
		put(DvcSts.SD_NRML,      DvcSts.NRML);
		put(DvcSts.SD_TRBL,      DvcSts.TRBL);
	}};
}
