package com.florin;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/customers")
@AllArgsConstructor
public class CustomerController {
    private final CustomerRepository customerRepository;

    @GetMapping
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    record NewCustomerRequest(String name, String email, int age) {
    }

    @PostMapping
    public ResponseEntity<?> addCustomer(@RequestBody NewCustomerRequest request) {
        try {
            if (customerRepository.findStudentByEmail(request.email()).isPresent()) {
                return new ResponseEntity<>("Error: email already exists ", HttpStatus.CONFLICT);
            }
            Customer customer = new Customer();
            customer.setName(request.name());
            customer.setEmail(request.email());
            customer.setAge(request.age);
            customerRepository.save(customer);
            return new ResponseEntity<Customer>(customer, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{customerID}")
    public ResponseEntity<?> deleteCustomerById(@PathVariable("customerID") int id) {
        if (customerRepository.findById(id).isPresent()) {
            customerRepository.deleteById(id);
            return new ResponseEntity<>("Customer with id " + id + " was successfully deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error: no customer found with id " + id, HttpStatus.NOT_FOUND);
    }

    @PutMapping("{customerID}")
    public ResponseEntity<?> updateCustomerById(@PathVariable("customerID") int id, @RequestBody Customer customer) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        if (customerOptional.isPresent()) {
            Customer customerToSave = customerOptional.get();
            customerToSave.setName(customer.getName() != null ? customer.getName() : customerToSave.getName());
            customerToSave.setEmail(customer.getEmail() != null ? customer.getEmail() : customerToSave.getEmail());
            customerToSave.setAge(customer.getAge() != 0 ? customer.getAge() : customerToSave.getAge());
            customerRepository.save(customerToSave);
            return new ResponseEntity<>("Customer with id " + id + " was successfully updated", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Error: no customer found with id " + id, HttpStatus.NOT_FOUND);
        }
    }
}
