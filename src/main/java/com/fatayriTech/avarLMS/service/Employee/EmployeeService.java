package com.fatayriTech.avarLMS.service.Employee;

import com.fatayriTech.avarLMS.exceptions.AlreadyExistsException;
import com.fatayriTech.avarLMS.exceptions.ResourceNotFoundException;
import com.fatayriTech.avarLMS.model.User;
import com.fatayriTech.avarLMS.model.*;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.PositionRepo;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeInviteRepo;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.repository.NationalityRepo;
import com.fatayriTech.avarLMS.repository.SendingEmails.EmailQueueRepository;
import com.fatayriTech.avarLMS.repository.UserRepo;
import com.fatayriTech.avarLMS.request.Employees.CreateEmployeeRequest;
import com.fatayriTech.avarLMS.request.Employees.LinkEmployeeUserRequest;
import com.fatayriTech.avarLMS.request.Employees.UpdateEmployeeRequest;
import com.fatayriTech.avarLMS.response.employee.EmployeeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepo employeeRepo;
    private final DepartmentRepo departmentRepo;
    private final PositionRepo positionRepo;
    private final UserRepo userRepo;

    private final NationalityRepo nationalityRepo;
    private final EmailQueueRepository emailQueueRepo;
    private final EmployeeInviteRepo employeeInviteRepo;

    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        if (employeeRepo.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Employee already exists with email: " + request.getEmail());
        }

        Department department = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Position position = positionRepo.findById(request.getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("Position not found"));

        Employee manager = null;
        if (request.getManagerId() != null) {
            manager = employeeRepo.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        }



        Nationality nationality = null;
        if (request.getNationalityId() != null) {
            nationality = nationalityRepo.findById(request.getNationalityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Nationality not found"));
        }

        Employee employee = new Employee();
        employee.setEmployeeId(request.getEmployeeId());
        employee.setEmail(request.getEmail());
        employee.setFirstName(request.getFirstName());
        employee.setMiddleName(request.getMiddleName());
        employee.setLastName(request.getLastName());
        employee.setDepartment(department);
        employee.setPosition(position);
        employee.setManager(manager);

        employee.setNationality(nationality);
        employee.setGender(request.getGender());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setActive(true);

        return mapToResponse(employeeRepo.save(employee));
    }

    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        return mapToResponse(employee);
    }

    public EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (!employee.getEmail().equalsIgnoreCase(request.getEmail())
                && employeeRepo.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Employee already exists with email: " + request.getEmail());
        }

        Department department = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Position position = positionRepo.findById(request.getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("Position not found"));

        Employee manager = null;
        if (request.getManagerId() != null) {
            manager = employeeRepo.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        }



        Nationality nationality = null;
        if (request.getNationalityId() != null) {
            nationality = nationalityRepo.findById(request.getNationalityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Nationality not found"));
        }

        employee.setEmployeeId(request.getEmployeeId());
        employee.setEmail(request.getEmail());
        employee.setFirstName(request.getFirstName());
        employee.setMiddleName(request.getMiddleName());
        employee.setLastName(request.getLastName());
        employee.setDepartment(department);
        employee.setPosition(position);
        employee.setManager(manager);
        employee.setNationality(nationality);
        employee.setGender(request.getGender());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setActive(request.isActive());

        return mapToResponse(employeeRepo.save(employee));
    }

    public EmployeeResponse setEmployeeInactive(Long id) {
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        employee.setActive(false);

        return mapToResponse(employeeRepo.save(employee));
    }

    public EmployeeResponse linkEmployeeToUser(Long employeeId, LinkEmployeeUserRequest request) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employeeRepo.findByUsername(request.getUsername())
                .ifPresent(existingEmployee -> {
                    if (!existingEmployee.getId().equals(employeeId)) {
                        throw new RuntimeException("This username is already mapped to another employee");
                    }
                });

        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("No user found with username: " + request.getUsername()));

        employeeRepo.findByMasterUserId(user.getId())
                .ifPresent(existingEmployee -> {
                    if (!existingEmployee.getId().equals(employee.getId())) {
                        throw new RuntimeException("This user is already linked to another employee");
                    }
                });

        employee.setUsername(user.getUsername());
        employee.setMasterUserId(user.getId());

        return mapToResponse(employeeRepo.save(employee));
    }

    public EmployeeResponse unlinkEmployeeUser(Long employeeId) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setMasterUserId(null);
        employee.setUsername(null);

        return mapToResponse(employeeRepo.save(employee));
    }

    public List<EmployeeResponse> getEmployeesByManager(Long managerId) {
        return employeeRepo.findByManagerId(managerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteEmployee(Long id) {
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employeeRepo.delete(employee);
    }

    public EmployeeResponse inviteEmployee(Long employeeId) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (employee.getUsername() != null && !employee.getUsername().isBlank()) {
            throw new RuntimeException("Employee is already mapped to a user");
        }

        EmployeeInvite invite = new EmployeeInvite();
        invite.setEmployee(employee);
        invite.setToken(java.util.UUID.randomUUID().toString());
        invite.setExpiresAt(java.time.LocalDateTime.now().plusDays(7));
        invite.setUsed(false);

        employeeInviteRepo.save(invite);

        String signupLink = "http://localhost:5173/signup?inviteToken=" + invite.getToken();

        String subject = "You are invited to AVAR";

        String body = """
                Hello %s,

                You have been invited to join AVAR Facility Management.

                Please create your account using this link:
                %s

                Best regards,
                AVAR Team
                """.formatted(buildFullName(employee), signupLink);

        EmailQueue email = new EmailQueue();
        email.setToEmail(employee.getEmail());
        email.setSubject(subject);
        email.setBody(body);

        emailQueueRepo.save(email);

        return mapToResponse(employee);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        String fullName = buildFullName(employee);

        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeId(),
                employee.getEmail(),
                employee.getFirstName(),
                employee.getMiddleName(),
                employee.getLastName(),
                fullName,

                employee.getDepartment() != null ? employee.getDepartment().getId() : null,
                employee.getDepartment() != null ? employee.getDepartment().getName() : null,

                employee.getPosition() != null ? employee.getPosition().getId() : null,
                employee.getPosition() != null ? employee.getPosition().getName() : null,

                employee.getManager() != null ? employee.getManager().getId() : null,
                employee.getManager() != null ? buildFullName(employee.getManager()) : null,

                //employee.getTerritory() != null ? employee.getTerritory().getId() : null,
                //employee.getTerritory() != null ? employee.getTerritory().getName() : null,

                employee.getNationality() != null ? employee.getNationality().getId() : null,
                employee.getNationality() != null ? employee.getNationality().getName() : null,

                employee.getGender(),

                employee.getPhoneNumber(),
                employee.isActive(),

                employee.getMasterUserId(),
                employee.getUsername(),

                employee.getCreationDate(),
                employee.getModifiedDate()
        );
    }

    private String buildFullName(Employee employee) {
        return String.join(" ",
                employee.getFirstName() != null ? employee.getFirstName() : "",
                employee.getMiddleName() != null ? employee.getMiddleName() : "",
                employee.getLastName() != null ? employee.getLastName() : ""
        ).trim().replaceAll(" +", " ");
    }
}