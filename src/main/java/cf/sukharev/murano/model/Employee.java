package cf.sukharev.murano.model;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Represents employee entity.
 */
@Data
@Where(clause = "deleted = 0")
@Entity
@Builder
@ToString(exclude = "department")
@EqualsAndHashCode(exclude = "department")
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private final static String PHONE_NUMBER_REGEXP = "\\(\\d{3}\\)-\\d{3}-\\d{2}-\\d{2}";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_id_seq")
    @SequenceGenerator(name = "employee_id_seq", initialValue = 100)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String firstName;

    @Column(nullable = false)
    @NotNull
    private String lastName;

    @Column(nullable = false, unique = true)
    @NotNull
    @Pattern(regexp = PHONE_NUMBER_REGEXP, message = "Invalid phone number.")
    private String phone;

    @Column(nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT FALSE")
    @Builder.Default
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    public Long getDepartmentId() {
        return this.department != null ? this.department.getId() : null;
    }

    public boolean belongsToDepartment(Long departmentId) {
        Long currentDepartmentId = this.getDepartmentId();
        return currentDepartmentId != null && currentDepartmentId.equals(departmentId);
    }
}
