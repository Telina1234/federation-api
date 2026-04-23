package mg.federation.federationapi.controller;

import mg.federation.federationapi.dto.CreateMember;
import mg.federation.federationapi.dto.Member;
import mg.federation.federationapi.service.MemberService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    @PostMapping
    public List<Member> create(@RequestBody List<CreateMember> requests) {
        return service.create(requests);
    }
}