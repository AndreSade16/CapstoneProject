package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.IngredientDefinition;
import andreasaderi.capstone.exceptions.NotFoundException;
import andreasaderi.capstone.exceptions.RecordAlreadyExistsException;
import andreasaderi.capstone.repositories.IngredientDefinitionRepository;
import andreasaderi.capstone.requestDTOs.IngredientDefinitionDTO;
import andreasaderi.capstone.requestDTOs.IngredientDefinitionFiltersDTO;
import andreasaderi.capstone.specifications.IngredientDefinitionSpecification;
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
public class IngredientDefinitionService {

    private final IngredientDefinitionRepository ingredientDefinitionRepository;
    private final CloudinaryService cloudinaryService;
    private final IngredientDefinitionSpecification ingredientDefinitionSpecification;

    public IngredientDefinitionService(IngredientDefinitionRepository ingredientDefinitionRepository, CloudinaryService cloudinaryService, IngredientDefinitionSpecification ingredientDefinitionSpecification) {
        this.ingredientDefinitionRepository = ingredientDefinitionRepository;
        this.cloudinaryService = cloudinaryService;
        this.ingredientDefinitionSpecification = ingredientDefinitionSpecification;
    }


    public IngredientDefinition save(IngredientDefinitionDTO body, MultipartFile ingredientImage) {
        if (ingredientDefinitionRepository.existsByName(body.name()))
            throw new RecordAlreadyExistsException("Ingredient definition with name '" + body.name() + "' already exists");

        String imageUrl = cloudinaryService.uploadValidatedImageAndGetUrl(ingredientImage);
        return ingredientDefinitionRepository.save(new IngredientDefinition(body.name(), body.description(), imageUrl, body.category(), body.unit(), body.defaultStorageLocation(), body.shelfLifeDays(), body.alternativeUsages(), body.seasonality()));
    }

    public IngredientDefinition findById(UUID ingredientDefinitionId) {
        return ingredientDefinitionRepository.findById(ingredientDefinitionId).orElseThrow(() -> new NotFoundException("Ingredient definition with ID '" + ingredientDefinitionId + "' not found"));
    }

    public Page<IngredientDefinition> findAll(int page, int size, String sortBy, Sort.Direction direction, @Valid IngredientDefinitionFiltersDTO filters) {
        if (size <= 0) size = 10;
        if (size > 20) size = 20;
        if (page < 0) page = 0;

        Sort sort = Sort.by(direction, sortBy);

        if (!sortBy.equals("id")) {
            sort = sort.and(Sort.by(direction, "IngredientDefinitionId"));
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<IngredientDefinition> spec = ingredientDefinitionSpecification.specificationIngredientDefinitionBuilder(filters);


        return ingredientDefinitionRepository.findAll(spec, pageable);
    }


    public IngredientDefinition updateById(UUID ingredientDefinitionId, IngredientDefinitionDTO body, MultipartFile ingredientImage) {
        IngredientDefinition ingredientDefinition = findById(ingredientDefinitionId);

        if (!ingredientDefinition.getName().equalsIgnoreCase(body.name())
                && ingredientDefinitionRepository.existsByName(body.name())) {
            throw new RecordAlreadyExistsException("Ingredient definition named '" + body.name() + "' already exists");
        }

        if (ingredientImage != null && !ingredientImage.isEmpty()) {

            String imageUrl = cloudinaryService.uploadValidatedImageAndGetUrl(ingredientImage);

            ingredientDefinition.setImageUrl(imageUrl);
        }


        ingredientDefinition.setName(body.name());
        ingredientDefinition.setDescription(body.description());
        ingredientDefinition.setCategory(body.category());
        ingredientDefinition.setUnit(body.unit());
        ingredientDefinition.setDefaultStorageLocation(body.defaultStorageLocation());
        ingredientDefinition.setShelfLifeDays(body.shelfLifeDays());
        ingredientDefinition.setAlternativeUsages(body.alternativeUsages());
        ingredientDefinition.setSeasonality(body.seasonality());

        return ingredientDefinitionRepository.save(ingredientDefinition);


    }

    public void delete(UUID ingredientDefinitionId) {
        IngredientDefinition ingredientDefinition = findById(ingredientDefinitionId);
        ingredientDefinitionRepository.delete(ingredientDefinition);
    }
}

