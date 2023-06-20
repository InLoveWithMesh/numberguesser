package com.example.numberguesser2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface gameRepository extends JpaRepository<game, Long>{

}
