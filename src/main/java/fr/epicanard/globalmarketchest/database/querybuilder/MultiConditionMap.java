package fr.epicanard.globalmarketchest.database.querybuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

/**
 * List that store key, value, and ConditionType for query composition
 */
public class MultiConditionMap {
  @Getter
  private List<ConditionStructure> map = new ArrayList<ConditionStructure>();

  public List<String> keys() {
    return this.map.stream().map(ConditionStructure::getKey).collect(Collectors.toList());
  }

  public List<Object> values() {
    return this.map.stream().map(ConditionStructure::getValue).collect(Collectors.toList());
  }

  public Integer size() {
    return this.map.size();
  }

  public ConditionStructure get(int index) {
    return this.map.get(index);
  }

  public String getKey(int index) {
    return this.map.get(index).getKey();
  }

  public Object getValue(int index) {
    return this.map.get(index).getValue();
  }

  public void put(ConditionStructure condition) {
    this.map.add(condition);
  }

  public void put(String key, Object value, ConditionType type) {
    this.map.add(new ConditionStructure(key, value, type));
  }

  public void clear() {
    this.map.clear();
  }
}