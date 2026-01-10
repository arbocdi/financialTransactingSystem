* Test that:
    - creates 2n accounts using m threads
    - deposits 55 to n accounts
    - makes 10 transfers from source n to target n accounts, total transfer amount = 55

Progress monitoring (all events should be sent):

```sql
select COUNT(*)                                 AS total,
       COUNT(*) FILTER (WHERE e.state = 'SENT') AS sent,
       COUNT(*) FILTER (WHERE e.state = 'NEW')  AS new
from outbox_events e;
```

Correctness (should return 0 records):

```sql
select *
from accounts a
where a.balance not in (0, 55);
```