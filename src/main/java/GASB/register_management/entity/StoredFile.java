package GASB.register_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "stored_file")
public class StoredFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "salted_hash", columnDefinition = "TEXT", nullable = false)
    private String saltedHash;

    @Column(name = "size")
    private int size;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name="save_path",columnDefinition = "TEXT", nullable = false)
    private String savePath;

    @JsonIgnore
    @OneToOne(mappedBy = "storedFile", cascade = CascadeType.ALL)
    private VtReport vtReport;

    @JsonIgnore
    @OneToOne(mappedBy = "storedFile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private FileStatus fileStatus;

    @JsonIgnore
    @OneToOne(mappedBy = "storedFile", cascade = CascadeType.ALL)
    private Gscan scanTable;

    @JsonIgnore
    @OneToOne(mappedBy = "storedFile", cascade = CascadeType.ALL)
    private DlpReport dlpReport;

    @OneToMany(mappedBy = "storedFile")
    private List<FileUpload> fileUploads;
}
