package ideal.worldcup.automoney.util;

public class Config {
	/** �α� ��� ���� */
	private static boolean LogVisible = false;
	//private static boolean LogVisible = true;

	/** �α׿� �Բ� ����� TAG MSG */
	private static String LogTag = "ModooStar";

	/*************************************************************
	 * �α� ��� ���θ� ���Ѵ�.
	 *
	 * @return boolean
	 *************************************************************/
	public static final boolean isLogVisible() {
		return LogVisible;
	}

	/*************************************************************
	 * �α� ��� ���θ� �����Ѵ�.
	 *
	 * @param logVisible
	 *            ���� ���� (boolean)
	 *************************************************************/
	public static final void setLogVisible(boolean logVisible) {
		LogVisible = logVisible;
	}

	/*************************************************************
	 * �α׿� ����� �±׸� ���Ѵ�.
	 *
	 * @return String
	 *************************************************************/
	public static final String getLogTag() {
		return LogTag;
	}

	/*************************************************************
	 * �α׿� ����� �±׸� �����Ѵ�.
	 *
	 * @param logTag
	 *            �±�
	 *************************************************************/
	public static final void setLogTag(String logTag) {
		LogTag = logTag;
	}
}
