package exception;

/**
 * Created by taihuynh on 10/7/16.
 */
public class DownloadException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * DownloadException summarizes error occurs 
	 * process that causes download failures.
	 * 
	 * @param message
	 */
	public DownloadException(String message) {
        super(message);
    }

}
