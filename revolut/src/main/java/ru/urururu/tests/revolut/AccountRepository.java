package ru.urururu.tests.revolut;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public class AccountRepository {
    private AtomicLong nextAccountId;
    private AtomicLong nextTransferId;
    private Map<Long, Account> accounts;

    private AccountRepository() {
        reset();
    }

    public Account add() {
        Account account = new Account(nextAccountId.getAndIncrement());

        accounts.put(account.getId(), account);

        return account;
    }

    public Collection<Account> list() {
        return accounts.values();
    }

    public static AccountRepository getInstance() {
        return Holder.INSTANCE;
    }

    public void setBalance(long accountId, long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount: " + amount);
        }

        Account account = getAccount(accountId);

        synchronized (account) {
            account.setBalance(amount);
        }
    }

    public long transfer(long fromId, long toId, long amount) {
        if (fromId > toId) {
            // ordering ids, to prevent deadlocks
            long temp = fromId;
            fromId = toId;
            toId = temp;
            amount = -amount;
        }

        Account from = getAccount(fromId);
        Account to = getAccount(toId);

        synchronized (from) {
            synchronized (to) {
                if (from.getBalance() - amount < 0) {
                    throw new IllegalArgumentException("balance too low for: " + fromId);
                }

                if (to.getBalance() + amount < 0) {
                    throw new IllegalArgumentException("balance too low for: " + toId);
                }

                from.setBalance(from.getBalance() - amount);
                to.setBalance(to.getBalance() + amount);
            }
        }

        return nextTransferId.getAndIncrement();
    }

    private Account getAccount(long fromId) {
        Account account = accounts.get(fromId);

        if (account == null) {
            throw new IllegalArgumentException("no user with id: " + fromId);
        }

        return account;
    }

    @Deprecated
    void reset() {
        nextAccountId = new AtomicLong(1);
        nextTransferId = new AtomicLong(1);
        accounts = new ConcurrentHashMap<>();
    }

    private static class Holder {
        static final AccountRepository INSTANCE = new AccountRepository();
    }
}
