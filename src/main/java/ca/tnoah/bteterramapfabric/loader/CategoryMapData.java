package ca.tnoah.bteterramapfabric.loader;

import ca.tnoah.bteterramapfabric.gui.sidebar.DropdownCategory;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class CategoryMapData<T extends CategoryMapData.ICategoryMapProperty> {

    public final LinkedHashMap<String, DropdownCategory<T>> categoryMap;
    private int totalMapCount;

    @JsonCreator
    public CategoryMapData(LinkedHashMap<String, DropdownCategory<T>> categoryMap) {
        this.categoryMap = categoryMap;
        this.totalMapCount = 0;

        for (DropdownCategory<T> category : categoryMap.values())
            this.totalMapCount += category.size();
    }

    public DropdownCategory<T> getCategory(String categoryName) {
        return this.categoryMap.get(categoryName);
    }

    @Nullable
    public T getItem(String categoryName, String mapId) {
        DropdownCategory<T> category = this.categoryMap.get(categoryName);
        if (category == null) return null;
        return category.get(mapId);
    }

    public void setSource(String source) {
        for (DropdownCategory<T> category : this.categoryMap.values())
            for (T element: category.values())
                element.setSource(source);
    }

    public void append(CategoryMapData<T> other) {
        for(Map.Entry<String, DropdownCategory<T>> otherCategoryEntry : other.categoryMap.entrySet()) {
            String otherCategoryName = otherCategoryEntry.getKey();
            DropdownCategory<T> otherCategoryObject = otherCategoryEntry.getValue();

            DropdownCategory<T> existingCategory = getCategory(otherCategoryName);
            if(existingCategory != null) {
                existingCategory.putAll(otherCategoryObject);
            }
            else {
                this.categoryMap.put(otherCategoryName, otherCategoryObject);
            }
        }
        this.totalMapCount += other.totalMapCount;
    }

    public interface ICategoryMapProperty {
        void setSource(String source);
        String getSource();
    }
}
