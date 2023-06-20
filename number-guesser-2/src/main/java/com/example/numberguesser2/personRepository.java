package com.example.numberguesser2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface personRepository extends JpaRepository<person, Long>
{
    Optional<person> findByName(String name);
}