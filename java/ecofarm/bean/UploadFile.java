package ecofarm.bean;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.web.multipart.MultipartFile;

public class UploadFile {
	private String basePath;

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	public String uploadImage(MultipartFile file) {
		try {
			String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss-"));
			String photoName = date + file.getOriginalFilename();
			String photoPath = this.getBasePath() + File.separator + photoName;
			file.transferTo(new File(photoPath));
			return photoName;
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}
}
