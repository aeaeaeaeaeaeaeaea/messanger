package messenger.proj.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import messenger.proj.models.User;
import messenger.proj.repositories.UserRepository;
import messenger.proj.security.PersonDetails;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PersonDetailsServices implements UserDetailsService {

    private final UserRepository userRepository;
    @Autowired
    public PersonDetailsServices(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(s);

        if (!user.isPresent())
            throw new UsernameNotFoundException("User not found");

        return new PersonDetails(user.get());
    }
}