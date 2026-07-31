package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.*;
import andreasaderi.capstone.exceptions.NotFoundException;
import andreasaderi.capstone.exceptions.RecordAlreadyExistsException;
import andreasaderi.capstone.repositories.IngredientDefinitionRepository;
import andreasaderi.capstone.requestDTOs.IngredientDefinitionDTO;
import com.cloudinary.Cloudinary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IngredientDefinitionServiceTest {

    @Mock
    private IngredientDefinitionRepository ingredientDefinitionRepository;

    @Mock
    private Cloudinary fileUploader;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private IngredientDefinitionService ingredientDefinitionService;

    @Test
    void saveShouldReturnSavedIngredientWhenDTOIsValid() throws IOException {

        IngredientDefinitionDTO dto = new IngredientDefinitionDTO(
                "Tomato", "Fresh tomato description", Category.VEGETABLE, Unit.GRAMS,
                StorageLocation.PANTRY, 7, "Sauces", Set.of(Season.SUMMER)
        );

        IngredientDefinition savedEntity = new IngredientDefinition(
                dto.name(), dto.description(), "fake-img-url", dto.category(),
                dto.unit(), dto.defaultStorageLocation(), dto.shelfLifeDays(),
                dto.alternativeUsages(), dto.seasonality()
        );

        MockMultipartFile image = new MockMultipartFile(
                "image", "tomato.jpg", "image/jpeg", "fake-image-bytes".getBytes()
        );

        when(cloudinaryService.uploadValidatedImageAndGetUrl(image)).thenReturn("fake-img-url"); // adatta al nome/firma reale del metodo
        when(ingredientDefinitionRepository.existsByName(dto.name())).thenReturn(false);
        when(ingredientDefinitionRepository.save(any(IngredientDefinition.class))).thenReturn(savedEntity);

        IngredientDefinition result = ingredientDefinitionService.save(dto, image);

        assertNotNull(result);
        assertEquals("Tomato", result.getName());
        assertEquals(Category.VEGETABLE, result.getCategory());

        verify(ingredientDefinitionRepository).existsByName(dto.name());
        verify(ingredientDefinitionRepository).save(any(IngredientDefinition.class));
    }

    @Test
    void saveShouldThrowBadRequestExceptionWhenIngredientAlreadyExists() {
        IngredientDefinitionDTO dto = new IngredientDefinitionDTO(
                "Tomato", "Description...", Category.VEGETABLE, Unit.GRAMS,
                StorageLocation.PANTRY, 7, null, Set.of(Season.SUMMER)
        );
        MockMultipartFile image = new MockMultipartFile("image", "t.jpg", "image/jpeg", new byte[0]);

//        The name is already in the DB.
        when(ingredientDefinitionRepository.existsByName(dto.name())).thenReturn(true);

//        Does it throw a RecordAlreadyExistsException?
        assertThrows(RecordAlreadyExistsException.class, () -> ingredientDefinitionService.save(dto, image));

        // Has .save() method of ingredietDefinitionRepository been called in vain? We hope not!
        verify(ingredientDefinitionRepository, never()).save(any());
    }

    @Test
    void findByIdShouldThrowNotFoundExceptionWhenIdDoesNotExist() {
        UUID randomId = UUID.randomUUID();
        when(ingredientDefinitionRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> ingredientDefinitionService.findById(randomId));
    }

}
