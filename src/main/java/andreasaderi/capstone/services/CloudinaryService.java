package andreasaderi.capstone.services;

import andreasaderi.capstone.exceptions.FileNotAllowedException;
import andreasaderi.capstone.exceptions.FileUploadException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
public class CloudinaryService {

    private final Cloudinary fileUploader;

    public CloudinaryService(Cloudinary fileUploader) {
        this.fileUploader = fileUploader;
    }

    public String uploadValidatedImageAndGetUrl(MultipartFile ingredientImage) {

        String imageUrl;

        if (ingredientImage == null || ingredientImage.isEmpty()) {

            throw new FileNotAllowedException("You cannot upload a new ingredient without an image");

        }

        if (ingredientImage.getSize() >= 10485760)
            throw new FileNotAllowedException("File size can't be more than 10MB");
        if (!(Objects.equals(ingredientImage.getContentType(), "image/jpeg") || Objects.equals(ingredientImage.getContentType(), "image/gif") || Objects.equals(ingredientImage.getContentType(), "image/png") || Objects.equals(ingredientImage.getContentType(), "image/webp")))
            throw new FileNotAllowedException("File must be an img");


        try {
            Map result = fileUploader.uploader().upload(ingredientImage.getBytes(), ObjectUtils.emptyMap());
            imageUrl = (String) result.get("secure_url");
        } catch (IOException e) {
            throw new FileUploadException(e.getMessage());
        }

        return imageUrl;
    }
}
