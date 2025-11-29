package com.softserve.itacademy.service;

import com.softserve.itacademy.model.State;
import com.softserve.itacademy.config.exception.NullEntityReferenceException;
import com.softserve.itacademy.dto.StateDto;
import com.softserve.itacademy.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StateService {

    private final StateRepository stateRepository;

    public State create(State state) {
        if (state != null) {
            return stateRepository.save(state);
        }
        throw new NullEntityReferenceException("State cannot be 'null'");
    }

    public State readById(long id) {
        return stateRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("State with id " + id + " not found"));
    }

    public State update(State state) {
        if (state != null) {
            readById(state.getId());
            return stateRepository.save(state);
        }
        throw new NullEntityReferenceException("State cannot be 'null'");
    }

    public void delete(long id) {
        State state = readById(id);
        stateRepository.delete(state);
    }

    public List<State> getAll() {
        return stateRepository.findAllByOrderById();
    }

    public State getByName(String name) {
        Optional<State> optional = Optional.ofNullable(stateRepository.findByName(name));
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new EntityNotFoundException("State with name '" + name + "' not found");
    }

    public List<StateDto> findAll() {
        return stateRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private StateDto toDto(State state) {
        return StateDto.builder()
                .name(state.getName())
                .build();
    }
}
