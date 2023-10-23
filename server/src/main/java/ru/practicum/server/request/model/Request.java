package ru.practicum.server.request.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "requests")
@EqualsAndHashCode(exclude = "created")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor", referencedColumnName = "id")
    private User requestor;
    private LocalDateTime created;
    @OneToMany(mappedBy = "requestId", fetch = FetchType.LAZY)
    private List<Item> items = new ArrayList<>();
}
