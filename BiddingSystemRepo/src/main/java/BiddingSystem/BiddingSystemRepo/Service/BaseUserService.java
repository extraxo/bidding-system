package BiddingSystem.BiddingSystemRepo.Service;

import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface BaseUserService<T extends User> {

    //// ! HERE THERE IS NO METHOD CREATED BY JPA IN RUNTIME LIKE (find....) that's
    //// why all they work

    public T saveUser(T user) throws  Exception;

    public T getUserByEmail(String name) throws UsernameNotFoundException;

    List<T> getAll();

}
