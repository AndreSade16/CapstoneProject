package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.Recipe;
import andreasaderi.capstone.exceptions.NotFoundException;
import andreasaderi.capstone.exceptions.RecordAlreadyExistsException;
import andreasaderi.capstone.repositories.RecipeRepository;
import andreasaderi.capstone.requestDTOs.RecipeDTO;
import andreasaderi.capstone.requestDTOs.RecipeFiltersDTO;
import andreasaderi.capstone.specifications.RecipeSpecification;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeSpecification recipeSpecification;
    private final CloudinaryService cloudinaryService;

    public RecipeService(RecipeRepository recipeRepository, RecipeSpecification recipeSpecification, CloudinaryService cloudinaryService) {
        this.recipeRepository = recipeRepository;
        this.recipeSpecification = recipeSpecification;
        this.cloudinaryService = cloudinaryService;
    }

    public Recipe save(RecipeDTO body, MultipartFile recipeImage) {
        if (recipeRepository.existsByName(body.name()))
            throw new RecordAlreadyExistsException("A recipe named '" + body.name() + "' already exists");
        String imageUrl = cloudinaryService.uploadValidatedImageAndGetUrl(recipeImage);


        return recipeRepository.save(new Recipe(body.name(), body.description(), imageUrl, body.preparationTime(), body.cookingTime(), body.difficulty(), body.cost(), body.procedure()));
    }

    public Recipe findById(UUID recipeId) {
        return recipeRepository.findById(recipeId).orElseThrow(() -> new NotFoundException("Recipe with id '" + recipeId + "' not found"));
    }

    public Recipe updateById(UUID recipeId, RecipeDTO body, MultipartFile recipeImage) {
        Recipe recipe = findById(recipeId);

        if (!recipe.getName().equalsIgnoreCase(body.name())
                && recipeRepository.existsByName(body.name())) {
            throw new RecordAlreadyExistsException("Recipe named '" + body.name() + "' already exists");
        }

        if (recipeImage != null && !recipeImage.isEmpty()) {

            String imageUrl = cloudinaryService.uploadValidatedImageAndGetUrl(recipeImage);

            recipe.setImageUrl(imageUrl);
        }

        recipe.setName(body.name());
        recipe.setDescription(body.description());
        recipe.setPreparationTime(body.preparationTime());
        recipe.setCookingTime(body.cookingTime());
        recipe.setDifficulty(body.difficulty());
        recipe.setCost(body.cost());
        recipe.setProcedure(body.procedure());

        return recipeRepository.save(recipe);
    }

    public Recipe findByIdAndIncrementVisits(UUID id) {
        Recipe recipe = findById(id);

        recipe.setVisitsCount(recipe.getVisitsCount() + 1);

        return recipeRepository.save(recipe);
    }

    public Page<Recipe> findAll(int page, int size, String sortBy, Sort.Direction direction, @Valid RecipeFiltersDTO filters) {
        if (size <= 0) size = 10;
        if (size > 20) size = 20;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Specification<Recipe> spec = recipeSpecification.specificationRecipeBuilder(filters);


        return recipeRepository.findAll(spec, pageable);
    }
}

