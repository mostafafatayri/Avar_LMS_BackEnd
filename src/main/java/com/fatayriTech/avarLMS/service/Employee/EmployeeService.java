package com.fatayriTech.avarLMS.service.Employee;
import com.fatayriTech.avarLMS.enums.AcademyStatus;
import com.fatayriTech.avarLMS.enums.EmployeeType;
import com.fatayriTech.avarLMS.enums.EmploymentStatus;
import com.fatayriTech.avarLMS.repository.*;
import com.fatayriTech.avarLMS.response.employee.EmployeeCompanyInfoResponse;
import com.fatayriTech.avarLMS.exceptions.AlreadyExistsException;
import com.fatayriTech.avarLMS.exceptions.ResourceNotFoundException;
import com.fatayriTech.avarLMS.model.*;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.DepartmentRepo;
import com.fatayriTech.avarLMS.repository.DepartmentRepo.PositionRepo;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeInviteRepo;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import com.fatayriTech.avarLMS.repository.SendingEmails.EmailQueueRepository;
import com.fatayriTech.avarLMS.request.Employees.CreateEmployeeRequest;
import com.fatayriTech.avarLMS.request.Employees.LinkEmployeeUserRequest;
import com.fatayriTech.avarLMS.request.Employees.UpdateEmployeeRequest;
import com.fatayriTech.avarLMS.response.employee.EmployeeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeAddressRepo;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepo employeeRepo;
    private final DepartmentRepo departmentRepo;
    private final PositionRepo positionRepo;
    private final UserRepo userRepo;
    private final OrganizationRepo organizationRepo;
    private final NationalityRepo nationalityRepo;
    private final EmailQueueRepository emailQueueRepo;
    private final EmployeeInviteRepo employeeInviteRepo;
    private final EmployeeAddressRepo employeeAddressRepo;
    private final SubTeamRepo subTeamRepo;
    private final SpecializationRepo specializationRepo;
    private final SeniorityLevelRepo seniorityLevelRepo;
    private final LocationRepo locationRepo;
    public EmployeeResponse createEmployee(Long organizationId, CreateEmployeeRequest request) {
        if (employeeRepo.existsByEmailAndOrganizationId(request.getEmail(), organizationId)) {
            throw new AlreadyExistsException("Employee already exists with email: " + request.getEmail());
        }

        if (employeeRepo.existsByEmployeeIdAndOrganizationId(request.getEmployeeId(), organizationId)) {
            throw new AlreadyExistsException("Employee already exists with ID: " + request.getEmployeeId());
        }

        Organization organization = organizationRepo.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        Department department = departmentRepo.findByIdAndOrganizationId(request.getDepartmentId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Position position = positionRepo.findByIdAndOrganizationId(request.getPositionId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found"));
        SubTeam subTeam = null;
        if (request.getSubTeamId() != null) {
            subTeam = subTeamRepo.findByIdAndOrganizationId(request.getSubTeamId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Sub-Team not found"));
        }

        Specialization specialization = null;
        if (request.getSpecializationId() != null) {
            specialization = specializationRepo.findByIdAndOrganizationId(request.getSpecializationId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Specialization not found"));
        }

        SeniorityLevel seniorityLevel = null;
        if (request.getSeniorityLevelId() != null) {
            seniorityLevel = seniorityLevelRepo.findByIdAndOrganizationId(request.getSeniorityLevelId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Seniority level not found"));
        }

        Location location = null;
        if (request.getLocationId() != null) {
            location = locationRepo.findByIdAndOrganizationId(request.getLocationId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        }

        Employee manager = null;
        if (request.getManagerId() != null) {
            manager = employeeRepo.findByIdAndOrganizationId(request.getManagerId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        }

        Nationality nationality = null;
        if (request.getNationalityId() != null) {
            nationality = nationalityRepo.findById(request.getNationalityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Nationality not found"));
        }

        Employee employee = new Employee();
        employee.setOrganization(organization);
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
        employee.setSubTeam(subTeam);
        employee.setSpecialization(specialization);
        employee.setSeniorityLevel(seniorityLevel);
        employee.setLocation(location);

        employee.setEmploymentStatus(
                request.getEmploymentStatus() == null ? EmploymentStatus.ACTIVE : request.getEmploymentStatus()
        );
        employee.setAcademyStatus(
                request.getAcademyStatus() == null ? AcademyStatus.NOT_APPLICABLE : request.getAcademyStatus()
        );
        employee.setEmployeeType(
                request.getEmployeeType() == null ? EmployeeType.EXISTING_EMPLOYEE : request.getEmployeeType()
        );
        employee.setActive(employee.getEmploymentStatus() == EmploymentStatus.ACTIVE);
        return mapToResponse(employeeRepo.save(employee));
    }

    public List<EmployeeResponse> getAllEmployees(Long organizationId) {
        return employeeRepo.findByOrganizationId(organizationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EmployeeResponse getEmployeeById(Long organizationId, Long id) {
        Employee employee = employeeRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        return mapToResponse(employee);
    }

    public EmployeeResponse updateEmployee(Long organizationId, Long id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (!employee.getEmail().equalsIgnoreCase(request.getEmail())
                && employeeRepo.existsByEmailAndOrganizationId(request.getEmail(), organizationId)) {
            throw new AlreadyExistsException("Employee already exists with email: " + request.getEmail());
        }

        if (!employee.getEmployeeId().equalsIgnoreCase(request.getEmployeeId())
                && employeeRepo.existsByEmployeeIdAndOrganizationId(request.getEmployeeId(), organizationId)) {
            throw new AlreadyExistsException("Employee already exists with ID: " + request.getEmployeeId());
        }

        Department department = departmentRepo.findByIdAndOrganizationId(request.getDepartmentId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Position position = positionRepo.findByIdAndOrganizationId(request.getPositionId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found"));

        Employee manager = null;
        if (request.getManagerId() != null) {
            manager = employeeRepo.findByIdAndOrganizationId(request.getManagerId(), organizationId)
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

    public EmployeeResponse setEmployeeInactive(Long organizationId, Long id) {
        Employee employee = employeeRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        employee.setActive(false);
        return mapToResponse(employeeRepo.save(employee));
    }

    public EmployeeResponse linkEmployeeToUser(Long organizationId, Long employeeId, LinkEmployeeUserRequest request) {
        Employee employee = employeeRepo.findByIdAndOrganizationId(employeeId, organizationId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employeeRepo.findByUsernameAndOrganizationId(request.getUsername(), organizationId)
                .ifPresent(existingEmployee -> {
                    if (!existingEmployee.getId().equals(employeeId)) {
                        throw new RuntimeException("This username is already mapped to another employee");
                    }
                });

        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("No user found with username: " + request.getUsername()));

        employeeRepo.findByMasterUserIdAndOrganizationId(user.getId(), organizationId)
                .ifPresent(existingEmployee -> {
                    if (!existingEmployee.getId().equals(employee.getId())) {
                        throw new RuntimeException("This user is already linked to another employee");
                    }
                });

        employee.setUsername(user.getUsername());
        employee.setMasterUserId(user.getId());

        return mapToResponse(employeeRepo.save(employee));
    }

    public EmployeeResponse unlinkEmployeeUser(Long organizationId, Long employeeId) {
        Employee employee = employeeRepo.findByIdAndOrganizationId(employeeId, organizationId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setMasterUserId(null);
        employee.setUsername(null);

        return mapToResponse(employeeRepo.save(employee));
    }

    public List<EmployeeResponse> getEmployeesByManager(Long organizationId, Long managerId) {
        return employeeRepo.findByManagerIdAndOrganizationId(managerId, organizationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteEmployee(Long organizationId, Long id) {
        Employee employee = employeeRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employeeRepo.delete(employee);
    }

    public EmployeeResponse inviteEmployee(Long organizationId, Long employeeId) {
        Employee employee = employeeRepo.findByIdAndOrganizationId(employeeId, organizationId)
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

        String primaryAddress = employeeAddressRepo
                .findByOrganizationIdAndEmployeeIdAndPrimaryAddressTrueAndActiveTrue(
                        employee.getOrganization().getId(),
                        employee.getId()
                )
                .map(EmployeeAddress::getFullAddress)
                .orElse("-");

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

                employee.getNationality() != null ? employee.getNationality().getId() : null,
                employee.getNationality() != null ? employee.getNationality().getName() : null,

                primaryAddress,

                employee.getGender(),
                employee.getPhoneNumber(),
                employee.isActive(),

                employee.getMasterUserId(),
                employee.getUsername(),
                employee.getSubTeam() != null ? employee.getSubTeam().getId() : null,
                employee.getSubTeam() != null ? employee.getSubTeam().getName() : null,

                employee.getSpecialization() != null ? employee.getSpecialization().getId() : null,
                employee.getSpecialization() != null ? employee.getSpecialization().getName() : null,

                employee.getSeniorityLevel() != null ? employee.getSeniorityLevel().getId() : null,
                employee.getSeniorityLevel() != null ? employee.getSeniorityLevel().getName() : null,

                employee.getLocation() != null ? employee.getLocation().getId() : null,
                employee.getLocation() != null ? employee.getLocation().getName() : null,

                employee.getEmploymentStatus(),
                employee.getAcademyStatus(),
                employee.getEmployeeType(),
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
    public EmployeeCompanyInfoResponse getEmployeeCompanyInfo(
            Long organizationId,
            Long employeeId
    ) {
        Employee employee = employeeRepo.findByIdAndOrganizationId(employeeId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        return EmployeeCompanyInfoResponse.builder()
                .systemId(employee.getId())
                .employeeId(employee.getEmployeeId())

                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)

                .positionId(employee.getPosition() != null ? employee.getPosition().getId() : null)
                .jobTitle(employee.getPosition() != null ? employee.getPosition().getName() : null)

                .managerId(employee.getManager() != null ? employee.getManager().getId() : null)
                .managerName(employee.getManager() != null ? buildFullName(employee.getManager()) : null)

                .locationId(employee.getLocation() != null ? employee.getLocation().getId() : null)
                .locationName(employee.getLocation() != null ? employee.getLocation().getName() : null)

                .organizationId(employee.getOrganization() != null ? employee.getOrganization().getId() : null)
                .organizationName(employee.getOrganization() != null ? employee.getOrganization().getName() : null)

                .subTeamName(employee.getSubTeam() != null ? employee.getSubTeam().getName() : null)
                .specializationName(employee.getSpecialization() != null ? employee.getSpecialization().getName() : null)

                .employmentStatus(employee.isActive() ? "Active" : "Inactive")
                .creationDate(employee.getCreationDate())
                .modifiedDate(employee.getModifiedDate())
                .build();
    }
}