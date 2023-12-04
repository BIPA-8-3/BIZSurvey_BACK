package com.bipa.bizsurvey.domain.workspace.api;

import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.workspace.application.ContactService;
import com.bipa.bizsurvey.domain.workspace.dto.ContactDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspace/contact")
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<?> register(@AuthenticationPrincipal LoginUser loginUser,
                                      @RequestBody ContactDto.CreateRequest request) {
        ContactDto.Response response = contactService.create(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/list/{workspaceId}")
                .buildAndExpand(request.getWorkspaceId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDto.Response> readOne(@PathVariable Long id) {
        return ResponseEntity.ok().body(contactService.readOne(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<ContactDto.Response>> list(@AuthenticationPrincipal LoginUser loginUser,
                                                          ContactDto.SearchRequest request) {
        return ResponseEntity.ok().body(contactService.searchContacts(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> modify(@PathVariable Long id,
                                         @RequestBody ContactDto.UpdateRequest request) {
        contactService.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable Long id) {
        contactService.delete(id);
        return ResponseEntity.ok().body("삭제가 완료되었습니다.");
    }
}
