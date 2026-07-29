package andreasaderi.capstone.controllers;

import andreasaderi.capstone.entities.IngredientDefinition;
import andreasaderi.capstone.exceptions.ValidationException;
import andreasaderi.capstone.requestDTOs.IngredientDefinitionDTO;
import andreasaderi.capstone.responseDTOs.IngredientDefinitionCreatedDTO;
import andreasaderi.capstone.services.IngredientDefinitionService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientDefinitionController {

    private final IngredientDefinitionService ingredientDefinitionService;

    public IngredientDefinitionController(IngredientDefinitionService ingredientDefinitionService) {
        this.ingredientDefinitionService = ingredientDefinitionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IngredientDefinitionCreatedDTO createIngredient(@ModelAttribute @Validated IngredientDefinitionDTO body, @RequestPart(value = "ingredientImage") MultipartFile ingredientImage, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            throw new ValidationException(errorsList);
        }

        IngredientDefinition saved = ingredientDefinitionService.save(body, ingredientImage);

        return new IngredientDefinitionCreatedDTO(saved.getIngredientDefinitionId());
    }
}
