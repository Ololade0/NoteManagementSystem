package com.bookmanagement.bookmanagementsystem.service;


import com.bookmanagement.bookmanagementsystem.dto.response.CreateNoteResponse;
import com.bookmanagement.bookmanagementsystem.dto.response.UpdateNoteResponse;
import com.bookmanagement.bookmanagementsystem.dto.response.UserLoginResponse;
import com.bookmanagement.bookmanagementsystem.dto.response.UserRegisterResponse;
import com.bookmanagement.bookmanagementsystem.dao.model.Note;
import com.bookmanagement.bookmanagementsystem.dao.model.Role;
import com.bookmanagement.bookmanagementsystem.dao.model.RoleType;
import com.bookmanagement.bookmanagementsystem.dao.model.User;
import com.bookmanagement.bookmanagementsystem.dao.repository.UserRepository;
import com.bookmanagement.bookmanagementsystem.dto.request.*;
import com.bookmanagement.bookmanagementsystem.exception.NoteCannotBeFoundException;
import com.bookmanagement.bookmanagementsystem.exception.UserCannotBeFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;

    private final NoteService noteService;

    private final PasswordEncoder passwordEncoder;



    @Override
    public UserRegisterResponse registerUser(UserRegisterRequest userRegisterRequest) throws UserCannotBeFoundException {


        User user = new User();
        user.setEmail(userRegisterRequest.getEmail());
        user.setName(userRegisterRequest.getName());
        user.setPhonenumber(userRegisterRequest.getPhonenumber());
        user.setRoleHashSet(new HashSet<>());
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        user.getRoleHashSet().add(new Role(RoleType.USER));
        User savedUser = userRepository.save(user);


        UserRegisterResponse userRegisterResponse = new UserRegisterResponse();
        userRegisterResponse.setMessage("User successfully registered");
        userRegisterResponse.setUserId(savedUser.getId());
        userRegisterResponse.setEmail(savedUser.getEmail());
        userRegisterResponse.setPassword(savedUser.getPassword());
        user.getRoleHashSet().add(new Role(RoleType.USER));
        return userRegisterResponse;
    }

    @Override
    public User findById(Long userId) {
        Optional<User> foundUser =  userRepository.findUserById(userId);
        if(foundUser.isEmpty()){
            throw new UserCannotBeFoundException(UserCannotBeFoundException.UserCannotBeFoundException(userId));
        }
        else {
            return foundUser.get();

        }

    }

    @Override
    public long TotalUsers() {
        return userRepository.count();
    }

    @Override
    public String deleteAllUsers() {
        userRepository.deleteAll();
        return "All users successfully deleted";

    }



    @Override
    public Page<User> findAllUser(FindAllUserRequest findAllUserRequest) {
        Pageable pageable = PageRequest.of(findAllUserRequest.getPageNumber()-1, findAllUserRequest.getNumberOfPerPages());
        return userRepository.findAll(pageable);
    }

    @Override
    public String deleteUserById(Long userId) {
        Optional<User> foundUser = userRepository.findUserById(userId);
        if(foundUser.isPresent()){
            userRepository.deleteById(userId);
            return "User successfully deleted";
        }

        else {
            throw new UserCannotBeFoundException(UserCannotBeFoundException.UserCannotBeFoundException(userId));

        }
    }

    @Override
    public User updateUserProfile(UpdateUserProfileRequest updateUserProfileRequest) {
        Optional<User> foundUser =    userRepository.findUserById(updateUserProfileRequest.getUserId());
        if(foundUser.isPresent()){
            if(updateUserProfileRequest.getName() != null){
                foundUser.get().setName(updateUserProfileRequest.getName());
            }

            if(updateUserProfileRequest.getEmail() != null){
                foundUser.get().setEmail(updateUserProfileRequest.getEmail());
            }

            if(updateUserProfileRequest.getPhonenumber() != null){
                foundUser.get().setPhonenumber(updateUserProfileRequest.getPhonenumber());
            }
            return userRepository.save(foundUser.get());
        }

        else {
            throw new UserCannotBeFoundException(UserCannotBeFoundException.UserCannotBeFoundException(updateUserProfileRequest.getUserId()));
        }
    }

    @Override
    public CreateNoteResponse createNote(CreateNotesRequest createNotesRequest) {
        Note createdNote = noteService.createNoteBook(createNotesRequest);
        Optional<User> foundUser =   userRepository.findUserById(createNotesRequest.getUserId());
        if(foundUser.isEmpty()){
            throw new UserCannotBeFoundException(UserCannotBeFoundException.UserCannotBeFoundException(createdNote.getId()));
        }

        else {
            foundUser.get().getNoteList().add(createdNote);
            userRepository.save(foundUser.get());
        }
        CreateNoteResponse createNoteResponse = new CreateNoteResponse();
        createNoteResponse.setMessage("Note successfully created");
        createNoteResponse.setId(createdNote.getId());
        createNoteResponse.setContent(createdNote.getContent());
        return createNoteResponse;

    }

    @Override
    public long totalNotes() {
        return noteService.totalNoOfNotes();
    }

    @Override
    public String deleteAllNotes() {
        noteService.deleteAllNotes();
        return "All Notes deleted";
    }

    @Override
    public Note findNoteById(FindNoteByIdRequest findNoteByIdRequest) throws NoteCannotBeFoundException {
        Optional<User> foundUser = userRepository.findUserById(findNoteByIdRequest.getUserId());
        if (foundUser.isPresent()) {
            return noteService.findNoteById(findNoteByIdRequest.getNoteId());
        }
        throw new UserCannotBeFoundException(UserCannotBeFoundException.UserCannotBeFoundException(findNoteByIdRequest.getUserId()));


    }

    @Override
    public String deleteNoteById(DeleteNoteByIdRequest deleteNoteByIdRequest) throws NoteCannotBeFoundException{
        Optional<User> foundUser =  userRepository.findUserById(deleteNoteByIdRequest.getUserId());
        if(foundUser.isPresent()){
            noteService.deleteNoteById(deleteNoteByIdRequest.getNoteId());
            return "Note successfully deleted";
        }

        throw new UserCannotBeFoundException(UserCannotBeFoundException.UserCannotBeFoundException(deleteNoteByIdRequest.getUserId()));
    }

    @Override
    public UpdateNoteResponse updateNote(UpdateNoteRequest updateNoteRequest, Long noteId) throws NoteCannotBeFoundException {
        Optional<User> foundUser =    userRepository.findUserById(updateNoteRequest.getUserId());
        if(foundUser.isPresent()){
            noteService.updateNote(updateNoteRequest, noteId );
        }
        UpdateNoteResponse updateNoteResponse = new UpdateNoteResponse();
        updateNoteResponse.setMessage("Note Successfully Updated");

        return updateNoteResponse;
    }

    @Override
    public List<Note> findAllNotes(int page, int limit) {
        return noteService.findAllNote(page, limit);

    }

    @Override
    public User findUserByEmail(String username) {
        Optional<User> foundEmail =  userRepository.findUserByEmail(username);

        if(foundEmail.isPresent()){
            return foundEmail.get();
        }
        else {
            throw new UserCannotBeFoundException(UserCannotBeFoundException.UserCannotBeFoundException(username));
        }
    }



    @Override
    public List<User> findUserByNameAndNoteContent(String name, String content) {
        return userRepository.findUserByNameAndNoteList(name, content);
    }

    @Override
    public UserLoginResponse loginUser(UserLoginRequestModel userLoginRequestModel) {
        var user = userRepository.findUserByEmail(userLoginRequestModel.getEmail());
        if(user.isPresent() && user.get().getPassword().equals(userLoginRequestModel.getPassword()));
        return buildSuccessfulLoginResponse(user.get());
    }

    private UserLoginResponse buildSuccessfulLoginResponse(User user) {
        return UserLoginResponse.builder()
//                .code(201)
                .message("Login successful")
                .build();

    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username).orElse(null);
        if (user != null){
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), getAuthorities(user.getRoleHashSet()));
        }
        throw new UsernameNotFoundException("User with email "+ username +" does not exist");
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleType().name())).collect(Collectors.toSet());
    }
}

