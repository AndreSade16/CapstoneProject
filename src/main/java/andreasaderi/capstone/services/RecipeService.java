package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.Recipe;
import andreasaderi.capstone.exceptions.FileNotAllowedException;
import andreasaderi.capstone.exceptions.NotFoundException;
import andreasaderi.capstone.exceptions.RecordAlreadyExistsException;
import andreasaderi.capstone.repositories.RecipeRepository;
import andreasaderi.capstone.requestDTOs.RecipeDTO;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class RecipeService {

    public final RecipeRepository recipeRepository;
    public final Cloudinary fileUploader;

    public RecipeService(RecipeRepository recipeRepository, Cloudinary fileUploader) {
        this.recipeRepository = recipeRepository;
        this.fileUploader = fileUploader;
    }

    public Recipe save(RecipeDTO body, MultipartFile recipeImage) {
        if (recipeRepository.existsByName(body.name()))
            throw new RecordAlreadyExistsException("A recipe named '" + body.name() + "' already exists");
        String imageUrl;

        if (recipeImage.isEmpty()) {

            throw new FileNotAllowedException("You cannot upload a new ingredient without an image");

        } else {

            if (recipeImage.getSize() >= 10485760)
                throw new FileNotAllowedException("File size can't be more than 10MB");
            if (!(Objects.equals(recipeImage.getContentType(), "image/jpeg") || Objects.equals(recipeImage.getContentType(), "image/gif") || Objects.equals(recipeImage.getContentType(), "image/png") || Objects.equals(recipeImage.getContentType(), "image/webp")))
                throw new FileNotAllowedException("File must be an img");


            try {
                Map result = fileUploader.uploader().upload(recipeImage.getBytes(), ObjectUtils.emptyMap());
                imageUrl = (String) result.get("secure_url");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        return recipeRepository.save(new Recipe(body.name(), body.description(), imageUrl, body.preparationTime(), body.cookingTime(), body.difficulty(), body.cost(), body.procedure()));
    }

    public Recipe findById(UUID recipeId) {
        return recipeRepository.findById(recipeId).orElseThrow(() -> new NotFoundException("Recipe with id '" + recipeId + "' not found"));
    }
}

