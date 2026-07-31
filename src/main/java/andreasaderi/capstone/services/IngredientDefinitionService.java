package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.IngredientDefinition;
import andreasaderi.capstone.exceptions.FileNotAllowedException;
import andreasaderi.capstone.exceptions.NotFoundException;
import andreasaderi.capstone.exceptions.RecordAlreadyExistsException;
import andreasaderi.capstone.repositories.IngredientDefinitionRepository;
import andreasaderi.capstone.requestDTOs.IngredientDefinitionDTO;
import andreasaderi.capstone.requestDTOs.IngredientDefinitionFiltersDTO;
import andreasaderi.capstone.specifications.IngredientDefinitionSpecification;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class IngredientDefinitionService {

    private final IngredientDefinitionRepository ingredientDefinitionRepository;
    private final Cloudinary fileUploader;
    private final IngredientDefinitionSpecification ingredientDefinitionSpecification;

    public IngredientDefinitionService(IngredientDefinitionRepository ingredientDefinitionRepository, Cloudinary fileuploader, IngredientDefinitionSpecification ingredientDefinitionSpecification) {
        this.ingredientDefinitionRepository = ingredientDefinitionRepository;
        this.fileUploader = fileuploader;
        this.ingredientDefinitionSpecification = ingredientDefinitionSpecification;
    }

    public IngredientDefinition save(IngredientDefinitionDTO body, MultipartFile ingredientImage) {
        if (ingredientDefinitionRepository.existsByName(body.name()))
            throw new RecordAlreadyExistsException("Ingredient definition with name '" + body.name() + "' already exists");

        String imageUrl;

        if (ingredientImage.isEmpty()) {

            throw new FileNotAllowedException("You cannot upload a new ingredient without an image");

        } else {

            if (ingredientImage.getSize() >= 10485760)
                throw new FileNotAllowedException("File size can't be more than 10MB");
            if (!(Objects.equals(ingredientImage.getContentType(), "image/jpeg") || Objects.equals(ingredientImage.getContentType(), "image/gif") || Objects.equals(ingredientImage.getContentType(), "image/png") || Objects.equals(ingredientImage.getContentType(), "image/webp")))
                throw new FileNotAllowedException("File must be an img");


            try {
                Map result = fileUploader.uploader().upload(ingredientImage.getBytes(), ObjectUtils.emptyMap());
                imageUrl = (String) result.get("secure_url");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return ingredientDefinitionRepository.save(new IngredientDefinition(body.name(), body.description(), imageUrl, body.category(), body.unit(), body.defaultStorageLocation(), body.shelfLifeDays(), body.alternativeUsages(), body.seasonality()));
    }

    public IngredientDefinition findById(UUID ingredientDefinitionId) {
        return ingredientDefinitionRepository.findById(ingredientDefinitionId).orElseThrow(() -> new NotFoundException("Ingredient definition with ID '" + ingredientDefinitionId + "' not found"));
    }

    public Page<IngredientDefinition> findAll(int page, int size, String sortBy, Sort.Direction direction, @Valid IngredientDefinitionFiltersDTO filters) {
        if (size <= 0) size = 10;
        if (size > 20) size = 20;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Specification<IngredientDefinition> spec = ingredientDefinitionSpecification.specificationIngredientDefinitionBuilder(filters);


        return ingredientDefinitionRepository.findAll(spec, pageable);
    }
}
