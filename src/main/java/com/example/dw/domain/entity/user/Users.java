package com.example.dw.domain.entity.user;

import com.example.dw.domain.embedded.Address;
import com.example.dw.domain.entity.freeBoard.FreeBoard;
import com.example.dw.domain.entity.question.Question;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
//@Setter
@Table(name = "users")
public class Users {
    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String userAccount;
    private String userName;
    private String userPassword;
    private String userEmail;
    private String userPhone;

    @Default
    private LocalDateTime userJoinDate = LocalDateTime.now();
    private String userNickName;
    private String userIntroduction;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="zipCode", column = @Column(name="zip_code")),
            @AttributeOverride(name="address", column = @Column(name="address")),
            @AttributeOverride(name="detail", column = @Column(name="detail")),
    })
    private Address address;


    @OneToOne(mappedBy = "users" ,fetch = FetchType.LAZY)
    @JoinColumn(name = "user_file_id")
    private UserFile userFile;

    @OneToMany(mappedBy = "users")
    private List<Pet> pet = new ArrayList<>();

    @OneToMany(mappedBy = "users" ,fetch = FetchType.LAZY)
    private List<FreeBoard> freeBoard = new ArrayList<>();
    @OneToMany(mappedBy = "users",fetch = FetchType.LAZY)
    private List<Question> questions =new ArrayList<>();

    @Builder
    public Users(Long id, String userAccount, String userName, String userPassword, String userEmail, String userPhone,
                 LocalDateTime userJoinDate, String userNickName,
                 String userIntroduction, Address address,
                 UserFile userFile, List<Pet> pet,List<FreeBoard> freeBoard, List<Question> questions) {
        this.id = id;
        this.userAccount = userAccount;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userJoinDate = userJoinDate;
        this.userNickName = userNickName;
        this.userIntroduction = userIntroduction;
        this.address=address;
        this.userFile = userFile;
        this.pet = pet;
        this.freeBoard = freeBoard;
        this.questions= questions;
    }
}