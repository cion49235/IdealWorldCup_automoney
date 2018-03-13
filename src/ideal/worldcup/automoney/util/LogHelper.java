package ideal.worldcup.automoney.util;

import android.util.Log;

import java.util.Locale;

public class LogHelper {
    /**
     * �α� ��� ��� - �����
     */
    private static byte DEBUG = 0x00;

    /**
     * �α� ��� ��� - ����
     */
    private static byte ERROR = 0x01;

    /**
     * �α� ��� ��� - ����
     */
    private static byte INFO = 0x02;

    /**
     * �α� ��� ��� - ���
     */
    private static byte WARRING = 0x03;

    /*************************************************************
     * �α� ���
     *
     * @param type       �α� ���� (LogHelper.DEBUG, LogHelper.ERROR, LogHelper.INFO)
     * @param clsName Ŭ���� �̸�
     * @param msg       �α� �޽���
     *************************************************************/
    private static void print(byte type, String clsName, String msg) {
        if (Config.isLogVisible()) {

            String logMsg = String.format(Locale.getDefault(), "[%s] %s", clsName, msg);

            if (type == DEBUG) {
                Log.d(Config.getLogTag(), logMsg);
            } else if (type == ERROR) {
                Log.e(Config.getLogTag(), logMsg);
            } else if (type == WARRING) {
                Log.w(Config.getLogTag(), logMsg);
            } else {
                Log.i(Config.getLogTag(), logMsg);
            }
        }
    }

    /*************************************************************
     * ����� �α� ���
     *
     * @param cls �޼ҵ带 ȣ���� Ŭ����
     * @param format  �α� �޽��� ����
     * @param args  �Ķ����
     *************************************************************/
    public static void debug(Object cls, String format,
                             Object... args) {
        print(DEBUG, cls.getClass().getSimpleName(),
                String.format(Locale.getDefault(), format, args));
    }

    /*************************************************************
     * ���� �α� ���
     *
     * @param cls   �޼ҵ带 ȣ���� Ŭ����
     * @param format �α� �޽��� ����
     * @param args    �Ķ����
     *************************************************************/
    public static void error(Object cls, String format,
                             Object... args) {
        print(ERROR, cls.getClass().getSimpleName(),
                String.format(Locale.getDefault(), format, args));
    }

    /*************************************************************
     * �±װ� �ִ� ���� �α� ���
     *
     * @param cls   �޼ҵ带 ȣ���� Ŭ����
     * @param format �α� �޽��� ����
     * @param args    �Ķ����
     *************************************************************/
    public static void info(Object cls, String format,
                            Object... args) {
        print(INFO, cls.getClass().getSimpleName(),
                String.format(Locale.getDefault(), format, args));
    }

    /*************************************************************
     * �±װ� �ִ� ��� �α� ���
     *
     * @param cls �޼ҵ带 ȣ���� Ŭ����
     * @param format  �α� �޽��� ����
     * @param args  �Ķ����
     *************************************************************/
    public static void warring(Object cls, String format,
                               Object... args) {
        print(WARRING, cls.getClass().getSimpleName(),
                String.format(Locale.getDefault(), format, args));
    }
}
