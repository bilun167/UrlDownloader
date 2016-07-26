package downloader;

import exception.DownloadException;

import java.io.File;

/**
 * Functional interface to download file whose path exposes in the String url. 
 * The interface can be implemented differently by various protocols.
 *  
 * Created by taihuynh on 8/7/16.
 */
@FunctionalInterface
public interface Downloader {
	/**
	 * Taking a String that denotes a location of a file, download that file. 
	 * Handle fail downloads by retry. Take care of memory allocation of writing output during 
	 * transmission.
	 *  
	 * TODO: Partial download detector and handler.
	 *  
	 * The handler of partial download involves downloading to a temporary location. 
	 * Partial download occurs when exception happens at the client level. If such exception happens,
	 * don't copy the temporory file to the destination. If no exception occurs, I assume that the file
	 * is transferred completely.  
	 * @param url
	 * @return
	 * @throws DownloadException
	 */
    File download(String url) throws DownloadException;
}
