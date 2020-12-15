package ru.sberbank.demo1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static ru.sberbank.utils.Utils.*;

@Service
public class ItemsComparisonService {
    @Autowired
    ItemFetcher itemFetcher;

    ConcurrentHashMap<LocalDate, List<Item>> marketCache = new ConcurrentHashMap<>();
    ConcurrentHashMap<LocalDate, List<Item>> internetCache;

    public List<Comparison> compareForDate(LocalDate valueDate, LocalDate compareToDate) {
        Map<LocalDate, List<Item>> marketItemsByDates = getItemsFromMarket(valueDate, compareToDate);
        Map<LocalDate, List<Item>> internetItemsByDates = getItemsFromInternet(valueDate, compareToDate);

        return compareItems(marketItemsByDates, internetItemsByDates);
    }

    private Map<LocalDate, List<Item>> getItemsFromMarket(LocalDate valueDate, LocalDate compareToDate) {
        Map<LocalDate, List<Item>> item = new HashMap<>();
        LocalDate beginDate = valueDate;
        LocalDate endDate = valueDate;
        while (beginDate.isBefore(compareToDate)) {
            while (!marketCache.containsKey(beginDate)) {
                beginDate = beginDate.plusDays(1);
            }
            if (beginDate.isBefore(endDate)) {
                Map<LocalDate, List<Item>> fethced = itemFetcher.marketItems(beginDate, endDate);
                marketCache.putAll(fethced);
            }
            beginDate = beginDate.plusDays(1);
            endDate = beginDate;
        }
        return reMap(marketCache, (vd, it) -> filter(it, i -> i.weight == 0));
    }

    private Map<LocalDate, List<Item>> getItemsFromInternet(LocalDate valueDate, LocalDate compareToDate) {
        Map<LocalDate, List<Item>> itemsByDate = itemFetcher.internetItems(valueDate, compareToDate);
        return reMap(itemsByDate, (vd, it) -> filter(it, i -> "Apple".equals(i.name)));
    }

    private List<Comparison> compareItems(Map<LocalDate, List<Item>> marketItemsByDates,
                                          Map<LocalDate, List<Item>> internetItemsByDates) {
        return concat(marketItemsByDates.keySet().stream(), internetItemsByDates.keySet().stream())
                .distinct()
                .map(vd -> compareAndJoinEntities(marketItemsByDates.get(vd), internetItemsByDates.get(vd)))
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private List<Comparison> compareAndJoinEntities(List<Item> marketItem,
                                                    List<Item> internetItem) {
        Function<Function<Item, String>, List<Comparison>> compare = f -> compareEntities(marketItem, internetItem, f);
        List<Comparison> compareByName = compare.apply(Item::getName);
        List<Comparison> compareByStore = compare.apply(Item::getStore);
        return Stream.concat(compareByName.stream(), compareByStore.stream()).collect(toList());
    }

    private List<Comparison> compareEntities(List<Item> marketItems,
                                             List<Item> internetItems,
                                             Function<Item, String> getEntity) {
        Integer elementsSize = Math.max(marketItems.size(), internetItems.size());
        List<Comparison> comparisons = new ArrayList<>();
        for (int i = 0; i < elementsSize; i++) {

            String market = applyOrNull(getEntity, marketItems.get(i));
            String internet = applyOrNull(getEntity, internetItems.get(i));
            String marketNameStoreValue = "Market: " + market + "with weight:" + marketItems.get(i).weight;
            String internetNameStoreValue = "Internet: " + internet + "with value:" + marketItems.get(i).value;
            comparisons.add(Comparison.update(marketNameStoreValue, internetNameStoreValue));
        }
        return comparisons;
    }
}
