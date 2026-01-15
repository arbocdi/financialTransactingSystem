package kg.arbocdi.fts.test.api;

import kg.arbocdi.fts.test.complex.CreateAndTransferMultipleTest;
import kg.arbocdi.fts.test.complex.CreateAndTransferSingleTest;
import kg.arbocdi.fts.test.helpers.ConcurrencyHelper;
import kg.arbocdi.fts.test.simple.CreateAccounts;
import kg.arbocdi.fts.test.simple.CreateTransfers;
import kg.arbocdi.fts.test.simple.DepositAccounts;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final CreateAndTransferMultipleTest createAndTransferMultipleTest;
    private final CreateAndTransferSingleTest createAndTransferSingleTest;

    private final CreateAccounts createAccounts;
    private final DepositAccounts depositAccounts;
    private final CreateTransfers createTransfers;

    @PostMapping("/create-accounts")
    public CreateAccounts.Result createAccounts(
            @RequestParam("threads") int threads,
            @RequestParam("accounts") int accounts
    ) {
        return createAccounts.createAccounts(threads, accounts);
    }

    @PostMapping("/create-accounts-deposit")
    public List<ConcurrencyHelper.Result> createAndDepositAccounts(
            @RequestParam("threads") int threads,
            @RequestParam("sum") int sum,
            @RequestParam("accounts") int accounts
    ) {

        CreateAccounts.Result createResult = createAccounts.createAccounts(threads, accounts);
        DepositAccounts.Result depositResult = depositAccounts.depositAccounts(
                threads,
                createResult.getCreatedAccounts(),
                sum
        );
        ConcurrencyHelper.Result total = new ConcurrencyHelper.Result("total");
        total.merge(createResult);
        total.merge(depositResult);
        return List.of(createResult, depositResult, total);
    }

    @PostMapping("/create-accounts-deposit-transfer")
    public List<ConcurrencyHelper.Result> createAccountsDepositAndTransfer(
            @RequestParam("threads") int threads,
            @RequestParam("sum") int sum,
            @RequestParam("accounts") int accounts
    ) {

        CreateAccounts.Result from = createAccounts.createAccounts(threads, accounts);

        DepositAccounts.Result depositResult = depositAccounts.depositAccounts(
                threads,
                from.getCreatedAccounts(),
                sum
        );
        CreateAccounts.Result to = createAccounts.createAccounts(threads, accounts);
        List<CreateTransfers.Pair> pairs = new ArrayList<>(accounts);
        for (int i = 0; i < from.getCreatedAccounts().size(); i++) {
            pairs.add(new CreateTransfers.Pair(from.getCreatedAccounts().get(i), to.getCreatedAccounts().get(i)));
        }

        CreateTransfers.Result transferResult = createTransfers.createTransfers(threads, pairs);
        ConcurrencyHelper.Result total = new ConcurrencyHelper.Result("total");
        total.merge(from);
        total.merge(depositResult);
        total.merge(to);
        total.merge(transferResult);
        return List.of(from, depositResult, to, transferResult, total);
    }

    @PostMapping("/create-and-transfer-multiple")
    public CreateAndTransferMultipleTest.Result createAndTransferMultiple(
            @RequestParam("threads") int threads,
            @RequestParam("accounts") int accounts
    ) throws InterruptedException {
        return createAndTransferMultipleTest.createAccountsAndTransfer(threads, accounts);
    }

    @PostMapping("/create-and-transfer-single")
    public CreateAndTransferSingleTest.Result createAndTransferSingle(
    ) throws InterruptedException {
        return createAndTransferSingleTest.createAccountsAndTransfer();
    }
}
