package BiddingSystem.BiddingSystemRepo.Service;

import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.Repository.BaseUserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseUserServiceImpl<T extends User> implements BaseUserService<T> {

    private final BaseUserRepository<T> repository;
    private final PasswordEncoder passwordEncoder;

    public BaseUserServiceImpl(BaseUserRepository<T> repository, PasswordEncoder passwordEncoder){this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public T saveUser(T user) throws Exception {

        User existingUser = repository.findByEmail(user.getEmail());
        if (existingUser != null){
            throw new Exception("Username with tihs email aready exists");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        repository.save(user);

        return user;

    }

    @Override
    public T getUserByEmail(String email) throws UsernameNotFoundException {
        T user = repository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid id and password");
        }
        return user;
    }

    @Override
    public List<T> getAll() {
        return repository.findAll();
    }
}
