package DACN.DACN.validator;


import DACN.DACN.repository.IUserRepository;
import DACN.DACN.validator.annotation.ValidUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidUsernameValidator implements ConstraintValidator<ValidUsername, String> {
    @Autowired
    private IUserRepository userRepository;
    // kiểm tra xem username có tồn tại hay chưa (nếu username chưa tồn tại, nó sẽ được coi là hợp lệ)
    @Override
    public boolean isValid(String username, ConstraintValidatorContext context){
        if (userRepository == null)
            return true;
        return userRepository.findByUsername(username) == null;
    }
}
