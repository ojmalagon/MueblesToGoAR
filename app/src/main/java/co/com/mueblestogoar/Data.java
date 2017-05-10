package co.com.mueblestogoar;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ojmalagon on 10/04/2017.
 */

public class Data {
    public final static String[] ITEM_NAMES = {
            "MueblesToGoAR"
            };

    public final static Integer[] IMAGE_IDS = {
            R.drawable.mueblestogo};

    public final static String[] DESCRIPTIONS = {
            "Sofa 1 QWERT"};


    private List<Category> catalog;
    private List<Item> itemsCategory;



    public Data(){
        //
    }

    public void LoadData(){
        //Initi catalog & ítems

        ObjectMapper mapper = new ObjectMapper();
        catalog = new ArrayList<Category>();
        itemsCategory = new ArrayList<Item>();

        String a = "{\"id\":1,\"name\":\"Salas\",\"image\":"+R.drawable.salacategory1+","+
                    "\"items\":[{\"id\":1,\"description\":\"SSSSSSSSS\",\"name\":\"Experimenta\",\"dimensions\":\"1.70 X 1.20 x 0.70 cms\", \"image\":"+R.drawable.arview+"} ]" +
                   "}";
        Category cat=null;
        try
        {
            a = "{\"id\":1,\"name\":\"Sofás\",\"image\":"+R.drawable.salacategory1+"}";
            cat =  mapper.readValue(a, Data.Category.class);
            catalog.add(cat);
            a = "{\"id\":2,\"name\":\"Mesas\",\"image\":"+R.drawable.mesacategory+"}";
            cat =  mapper.readValue(a, Data.Category.class);
            catalog.add(cat);
            a = "{\"id\":3,\"name\":\"Comedores\",\"image\":"+R.drawable.comedorcategory+"}";
            cat =  mapper.readValue(a, Data.Category.class);
            catalog.add(cat);
            a = "{\"id\":4,\"name\":\"Camas\",\"image\":"+R.drawable.camacategory+"}";
            cat =  mapper.readValue(a, Data.Category.class);
            catalog.add(cat);
            a = "{\"id\":5,\"name\":\"Sillas\",\"image\":"+R.drawable.sillacategory+"}";
            cat =  mapper.readValue(a, Data.Category.class);
            catalog.add(cat);
            a = "{\"id\":6,\"name\":\"Escritorios\",\"image\":"+R.drawable.escritoriocategory+"}";
            cat =  mapper.readValue(a, Data.Category.class);
            catalog.add(cat);
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



/*
        //Category 1
        Category category = new Category();
        category.setName("Salas");
        category.setId((long) 1);
        category.setImage(R.drawable.salacategory);
        catalog.add(category);
        //Item 1
        Item item = new Item();
        item.setId((long) 1);
        item.setName("Sofá cama");
        item.setDimensions("1.70 X 1.20 x 0.70 cms");
        item.setImage(R.drawable.sofacama1);
        itemsCategory.add(item);
        //Item 2
        item = new Item();
        item.setId((long) 2);
        item.setName("Sofá cama extendido");
        item.setDimensions("1.70 X 1.20 x 0.70 cms");
        item.setImage(R.drawable.cama1);
        itemsCategory.add(item);
        //Set Items 1 y 2 to Category 1
        category.setItems(itemsCategory);

        itemsCategory = new ArrayList<Item>();
        //Category 2
        category = new Category();
        category.setName("Alcobas");
        category.setId((long) 1);
        category.setImage(R.drawable.alcobacategory);
        catalog.add(category);
        //Item 1
        item = new Item();
        item.setId((long) 1);
        item.setName("Alcoba 1");
        item.setDimensions("1.70 X 1.20 x 0.70 cms");
        item.setImage(R.drawable.sofacama1);
        itemsCategory.add(item);
        //Item 2
        item = new Item();
        item.setId((long) 2);
        item.setName("Alcoba 2");
        item.setDimensions("1.70 X 1.20 x 0.70 cms");
        item.setImage(R.drawable.cama1);
        itemsCategory.add(item);
        //Set Items 1 y 2 to Category 1
        category.setItems(itemsCategory);*/
    }

    public List<Category> getCatalog() {
        return catalog;
    }

    public void setCatalog(List<Category> catalog) {
        this.catalog = catalog;
    }

    public List<Item> getItemsCategoria() {
        return itemsCategory;
    }

    public void setItemsCategoria(List<Item> itemsCategory) {
        this.itemsCategory = itemsCategory;
    }


    public static class Item{

        private Long id;

        private String description;

        private String name;

        private String dimensions;

        private Integer image;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDimensions() {
            return dimensions;
        }

        public void setDimensions(String dimensions) {
            this.dimensions = dimensions;
        }

        public Integer getImage() {
            return image;
        }

        public void setImage(Integer image) {
            this.image = image;
        }
    }

    public static class Category {

        /*public Category(){
            super();
        }*/
        private List<Item> items;

        private Long id;

        private String name;

        private Integer image;

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getImage() {
            return image;
        }

        public void setImage(Integer image) {
            this.image = image;
        }
    }



}

