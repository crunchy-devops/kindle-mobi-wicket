package org.example;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends WebPage {
	private String uploadDir = "uploads";
	private Model<String> downloadLinkModel = new Model<>(null);

	public HomePage() {
		// File upload form
		Form<?> uploadForm = new Form<Void>("uploadForm") {
			private FileUploadField fileUploadField;

			@Override
			protected void onSubmit() {
				FileUpload upload = fileUploadField.getFileUpload();
				if (upload != null && upload.getClientFileName().endsWith(".mobi")) {
					try {
						java.io.File dir = new java.io.File(uploadDir);
						if (!dir.exists()) {
							dir.mkdirs(); // Create the directory if it doesn't exist
						}

						// Create a new file in the uploads directory
						File file = new File(dir, upload.getClientFileName());

						// Write the uploaded file to the server
						upload.writeTo(file);

						// Set the download link
						downloadLinkModel.setObject("/" + uploadDir + "/" + upload.getClientFileName());

					} catch (IOException e) {
						// Handle the exception if the file write fails
						error("File upload failed due to an I/O error: " + e.getMessage());
						e.printStackTrace();
					} catch (Exception e) {
						// Catch any other exception that might be thrown
						error("File upload failed: " + e.getMessage());
						e.printStackTrace();
					}
				} else {
					// Handle cases where no file was uploaded or wrong file type
					error("Please upload a MOBI file.");
				}
			}
		};

		uploadForm.setMultiPart(true);
		uploadForm.setMaxSize(Bytes.megabytes(10));

		FileUploadField fileUploadField = new FileUploadField("fileUpload");
		uploadForm.add(fileUploadField);
		add(uploadForm);

		// Display the uploaded files (MOBI) with download links
		add(new MultiLineLabel("uploadedFiles", Model.of(getUploadedFilesList())));

		// Download link label and ExternalLink
		Label downloadLabel = new Label("downloadLinkLabel", Model.of("Download uploaded file: "));
		ExternalLink downloadLink = new ExternalLink("downloadLink", downloadLinkModel, downloadLinkModel);
		add(downloadLabel);
		add(downloadLink);
	}

	/**
	 * Method to get the list of MOBI files from the uploads directory and format them for display.
	 *
	 * @return String representing the formatted list of file names and download links.
	 */
	private String getUploadedFilesList() {
		StringBuilder fileList = new StringBuilder();
		try {
			// Get the path to the uploads directory
			Path uploadPath = Paths.get(uploadDir);

			// Ensure the directory exists
			if (Files.exists(uploadPath)) {
				// List only .mobi files
				Files.list(uploadPath)
						.filter(path -> path.toString().endsWith(".mobi"))
						.forEach(path -> {
							String fileName = path.getFileName().toString();
							String downloadLink = "/" + uploadDir + "/" + fileName;
							fileList.append("File: ").append(fileName).append("\n");
							fileList.append("Download: ").append(downloadLink).append("\n\n");
						});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileList.length() > 0 ? fileList.toString() : "No MOBI files found.";
	}
}
