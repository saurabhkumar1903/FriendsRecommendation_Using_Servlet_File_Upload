package demopackage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// location to store file uploaded
	private static final String UPLOAD_DIRECTORY = "xxyy";

	// upload settings
	private static final int MEMORY_THRESHOLD 	= 1024 * 1024 * 3; 	// 3MB
	private static final int MAX_FILE_SIZE 		= 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE	= 1024 * 1024 * 50; // 50MB

	/**
	 * Upon receiving file upload submission, parses the request to read
	 * upload data and saves the file on disk.
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// checks if the request actually contains upload file
		if (!ServletFileUpload.isMultipartContent(request)) {
			// if not, we stop here
			PrintWriter writer = response.getWriter();
			writer.println("Error: Form must has enctype=multipart/form-data.");
			writer.flush();
			return;
		}

		// configures upload settings
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// sets memory threshold - beyond which files are stored in disk 
		factory.setSizeThreshold(MEMORY_THRESHOLD);
		// sets temporary location to store files
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

		ServletFileUpload upload = new ServletFileUpload(factory);
		
		// sets maximum size of upload file
		upload.setFileSizeMax(MAX_FILE_SIZE);
		
		// sets maximum size of request (include file + form data)
		upload.setSizeMax(MAX_REQUEST_SIZE);

		// constructs the directory path to store upload file
		// this path is relative to application's directory
		String uploadPath = getServletContext().getRealPath("")
				+ File.separator + UPLOAD_DIRECTORY;
		
		// creates the directory if it does not exist
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}

		try {
			// parses the request's content to extract file data
			@SuppressWarnings("unchecked")
			List<FileItem> formItems = upload.parseRequest(request);

			if (formItems != null && formItems.size() > 0) {
				// iterates over form's fields
				for (FileItem item : formItems) {
					// processes only fields that are not form fields
					if (!item.isFormField()) {
						String fileName = new File(item.getName()).getName();
						String filePath = uploadPath + File.separator + fileName;
						File storeFile = new File(filePath);
						System.out.println("File location: "+storeFile+"");
						// saves the file on disk
						item.write(storeFile);
						String temp_path=storeFile+"";
						Configuration conf = new Configuration();
					    // Replace CallJobFromServlet.class name with your servlet class
					        Job job = new Job(conf, "FileUploadServlet.class"); 
					        job.setJarByClass(FileUploadServlet.class);
					        job.setJobName("Friend_Recommendation");
					       
					        job.setOutputKeyClass(LongWritable.class);
					        job.setOutputValueClass(FriendCountWritable.class);
					       
					        job.setMapperClass(Map.class); // Replace Map.class name with your Mapper class
					        job.setReducerClass(Reduce.class); //Replace Reduce.class name with your Reducer class
					        
					       			        
					        job.setInputFormatClass(TextInputFormat.class);
					        job.setOutputFormatClass(TextOutputFormat.class);

					        // Job Input path
					        FileInputFormat.addInputPath(job, new  Path(temp_path)); 
					       
					        Path pathToBeDeleted = new  Path(temp_path);
					        File file=new File("/home/hadoopuser/Desktop/output");
					        boolean result = deleteDirectory(file);
					        
					       
					        // Job Output path
					    	FileOutputFormat.setOutputPath(job, new 
					        Path("/home/hadoopuser/Desktop/output")); 

					        try {
								job.waitForCompletion(true);
								System.out.println("JobStatus:"+job.waitForCompletion(true));
								if(job.waitForCompletion(true)==true)
								{
									request.setAttribute("output_path", "/home/hadoopuser/Desktop/output");
									getServletContext().getRequestDispatcher("/Processing").forward(
											request, response);
									return;
								}
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
						request.setAttribute("message",
							"Upload has been done successfully!");
					}
				}
			}
		} catch (Exception ex) {
			request.setAttribute("message",
					"There was an error: " + ex.getMessage());
		}
		// redirects client to message page
		getServletContext().getRequestDispatcher("/message.jsp").forward(
				request, response);
	}


	  boolean deleteDirectory(File directoryToBeDeleted) {
		System.out.println("In Delete Directory");
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}
	
}

