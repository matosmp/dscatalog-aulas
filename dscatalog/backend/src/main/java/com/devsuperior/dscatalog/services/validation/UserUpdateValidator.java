package com.devsuperior.dscatalog.services.validation;
import java.util.ArrayList;
import java.util.List;

import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.resources.exceptions.FieldMessage;
import org.springframework.beans.factory.annotation.Autowired;                                                              //Nome da Anotation e a classe que irá receber essa anotation
public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UserInsertValid ann) {
    }


    @Override
    public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {

        List<FieldMessage> list = new ArrayList<>();

        //Irá retornar null se não houver no banco de dados o email pesquisado
        User user = userRepository.findByEmail(dto.getEmail());

        // Realizar o teste para verificar se tem o email no banco de dados
        if (user != null) {
            list.add(new FieldMessage("email", "E-mail já existe"));
        }

        //Pegar a lista e inserir na lista de erros do beanValidation cada erro identificado
        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }
        return list.isEmpty(); //Se retornar true significa que não houve erro no objeto.
    }
}