package demo.test;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test5 {

	static String mainFolderPath = "/home/ued/Documents/workspace-BasicCrudStructure/BasicCrudStructure";
	static String classPath = "demo.models.";
	static String filePath = "src/demo/models";

	static List<String> listOfModelClasses = new ArrayList<>();

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		Map<String, List<Field>> modelNameWithItsVariableNames = new HashMap<>();

		String filePath = "src/demo/models";

		String classPath = "demo.models.";

		File folder = new File(filePath);

		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].isFile()) {

				String javaFileName = listOfFiles[i].getName();

				String className = javaFileName.split("\\.")[0];

				listOfModelClasses.add(className);

				try {

					Class c = Class.forName(classPath + className);

					List<Field> privateFieldsName = new ArrayList<>();
					Field[] allFields = c.getDeclaredFields();
					for (Field field : allFields) {
						if (Modifier.isPrivate(field.getModifiers())) {
							privateFieldsName.add(field);
						}
					}

					// setting of the value for model and its fields
					modelNameWithItsVariableNames.put(className, privateFieldsName);

					Boolean flag = Test5.createDTOs(filePath, className, privateFieldsName, classPath);

				} catch (ClassNotFoundException e) {

					e.printStackTrace();

				}
			}
		}

		Boolean flag1 = Test5.createModelSetterExtension(modelNameWithItsVariableNames);
		Boolean flag2 = Test5.createDTOSetterExtension(modelNameWithItsVariableNames);
		Boolean flag3 = Test5.createDAO(listOfModelClasses, modelNameWithItsVariableNames);

	}

	private static Boolean createDAO(List<String> listOfModelClasses,
			Map<String, List<Field>> modelNameWithItsVariableNames) {

		try {

			for (Map.Entry<String, List<Field>> iterable_element : modelNameWithItsVariableNames.entrySet()) {

				String model = iterable_element.getKey(); // For model name
				List<Field> fieldList = iterable_element.getValue(); // For model fields List
				String param = " ";
				List<String> idFields = getParamFields(fieldList);
				for (String parameters : idFields) {
					param = "String " + parameters + ", " + param;
				}

				String daoPath = Test5.createNewFolders(mainFolderPath, filePath, "Dao");
				// for (String model : listOfModelClasses) { // for model name

				File f = new File(daoPath + "/" + model + "Dao.java");

				PrintWriter printWriter = new PrintWriter(f);
				printWriter.println("package " + Test5.classPath.split("\\.")[0] + ".Dao;");
				printWriter.println();
				printWriter.println("import java.util.List;");
				printWriter.println("import demo.test.DatabaseHelper;");
				printWriter.println("import demo.models." + model + ";");
				printWriter.println("import demo.test.BaseDao;");
				printWriter.println();
				printWriter.println("public interface " + model + "Dao extends BaseDao<" + model + ">{");
				printWriter.println();
				printWriter
						.println("\tList<" + model + "> get(String id, " + param + "DatabaseHelper databaseHelper);");
				printWriter.println("}");
				printWriter.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
//			List<Article> getAll(String artId, DatabaseHelper databaseHelper);
	}

	// dto.setQuestionsDTO(getQuestionsDTO(model.getQuestions()));

	private static List<String> getParamFields(List<Field> fieldList) {

		List<String> list = new ArrayList<>();
		list.add("java.lang.String");
		list.add("java.lang.Integer");
		list.add("java.lang.Long");
		list.add("java.lang.Double");
		list.add("java.lang.Float");
		list.add("java.lang.Boolean");
		list.add("java.util.Date");
		list.add("long");

		List<String> parameters = new ArrayList<>();
		parameters.clear();
		for (Field field : fieldList) {

//			if (field.getName().toLowerCase().contains("Id".toLowerCase())) {
//				parameters.add(field.getName().substring(0, 3) + "Id");
			if (!list.contains(field.getType().getName())) {
				parameters.add(field.getName().substring(0, 4) + "Id");
			}

		}
		return parameters;

	}

	private static Boolean createDTOSetterExtension(Map<String, List<Field>> modelNameWithItsVariableNames) {
		try {
			String ModelAndDTOSetterExtensionPath = Test5.createNewFolders(mainFolderPath, filePath,
					"ModelAndDTOSetterExtension");

			File f = new File(ModelAndDTOSetterExtensionPath + "/DtoSetterExtension.java");

			PrintWriter printWriter = new PrintWriter(f);
			printWriter.println("package " + Test5.classPath.split("\\.")[0] + ".ModelAndDTOSetterExtension;");
			printWriter.println();
			createModelSetterExtensionImports(listOfModelClasses, printWriter);
			printWriter.println();
			printWriter.println("class DTOSetterExtension {");
			printWriter.println();
			printWriter.println("\tHelperExtension helperExtension = new HelperExtension();");
			printWriter.println();
			createDtoSetterExtensionMethods(modelNameWithItsVariableNames, printWriter);

			printWriter.println("}");

			printWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private static void createDtoSetterExtensionMethods(Map<String, List<Field>> modelNameWithItsVariableNames,
			PrintWriter printWriter) {

		List<String> list = new ArrayList<>();
		list.add("java.lang.String");
		list.add("java.lang.Integer");
		list.add("java.lang.Long");
		list.add("java.lang.Double");
		list.add("java.lang.Float");
		list.add("java.lang.Boolean");
		list.add("java.util.Date");
		list.add("long");

		List<String> listName = new ArrayList<>();
		listName.add("isFlag");
		listName.add("createdOn");
		listName.add("updatedOn");

		for (Map.Entry<String, List<Field>> iterable_element : modelNameWithItsVariableNames.entrySet()) {

			String model = iterable_element.getKey(); // For model name
			List<Field> fieldList = iterable_element.getValue(); // For model fields List
			String mainValue = model.substring(0, 1).toLowerCase() + model.substring(1);

			printWriter.println("\tpublic " + model + "DTO get" + model + "DTO(" + model + " model) {");
			printWriter.println("\t\t" + model + "DTO dto = new " + model + "DTO();");

			if (fieldList.size() > 0) {

				printWriter.println("\t\tif (!helperExtension.isNullOrEmpty(model)) {");

				String id = "";

				String primaryKey = "";

				for (Field field : fieldList) {
					id = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);

					if (field.getName().toLowerCase().contains("Id".toLowerCase())) {

						primaryKey = id;

						printWriter.println("\t\t\tif (!helperExtension.isNullOrEmpty(model.get" + id + "()))");
						printWriter.println("\t\t\t\tdto.set" + id + "(model.get" + id + "());");

					} else if (field.getType().getName().equalsIgnoreCase("java.util.Date")
							&& listName.contains(field.getName())) {
						System.out.println("==$$$" + field.getName());

						printWriter.println("\t\t\tif (!helperExtension.isNullOrEmpty(model.get" + id + "()))");
						printWriter.println("\t\t\t\tdto.set" + id + "(model.get" + id + "().getTime());");

					} else if (!list.contains(field.getType().getName())) {
						System.out.println("==" + field.getName());

						printWriter.println("\t\t\tdto.set" + id + "DTO(get" + id + "DTO(model.get" + id + "()));");
					} else if (!listName.contains(field.getName())) {
						System.out.println(field.getName());

						printWriter.println("\t\t\tdto.set" + id + "(model.get" + id + "());");
					}

				}
				printWriter.println("\t\t}");
			}

			printWriter.println("\t\treturn dto;");

			printWriter.println("\t}");
			printWriter.println();

		}

	}

	private static boolean createModelSetterExtension(Map<String, List<Field>> modelNameWithItsVariableNames) {

		try {
			String dtoFolderPath = Test5.createNewFolders(mainFolderPath, filePath, "ModelAndDTOSetterExtension");

			File f = new File(dtoFolderPath + "/ModelSetterExtension.java");

			PrintWriter printWriter = new PrintWriter(f);

			printWriter.println("package " + Test5.classPath.split("\\.")[0] + ".ModelAndDTOSetterExtension;");
			printWriter.println();
			createModelSetterExtensionImports(listOfModelClasses, printWriter);
			printWriter.println();
			printWriter.println("class ModelSetterExtension {");
			printWriter.println();

			printWriter.println("\tHelperExtension helperExtension = new HelperExtension();");
			printWriter.println();

			createModelSetterExtensionMethods(modelNameWithItsVariableNames, printWriter);

			printWriter.println("}");

			printWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static void createModelSetterExtensionImports(List<String> listOfModelsName, PrintWriter printWriter) {
//		import com.onlinetest.dto.AddressDetailsDTO;

//		classPath

		printWriter.println("import demo.test.HelperExtension;");

		for (String a : listOfModelsName) {

			printWriter.println("import " + classPath + a + ";");

			printWriter.println("import " + classPath.split("\\.")[0] + ".DTO." + a + "DTO;");

		}

	}

	private static void createModelSetterExtensionMethods(Map<String, List<Field>> modelNameWithItsVariableNames,
			PrintWriter printWriter) {

		List<String> list = new ArrayList<>();
		list.add("java.lang.String");
		list.add("java.lang.Integer");
		list.add("java.lang.Long");
		list.add("java.lang.Double");
		list.add("java.lang.Float");
		list.add("java.lang.Boolean");
		list.add("java.util.Date");
		list.add("long");

		List<String> listName = new ArrayList<>();
		listName.add("isFlag");
		listName.add("createdOn");
		listName.add("updatedOn");

		for (Map.Entry<String, List<Field>> iterable_element : modelNameWithItsVariableNames.entrySet()) {

			String model = iterable_element.getKey(); // For model name
			List<Field> fieldList = iterable_element.getValue(); // For model fields List
			String mainValue = model.substring(0, 1).toLowerCase() + model.substring(1);

			printWriter.println("\tpublic " + model + " get" + model + "(" + model + "DTO dto, " + model + " "
					+ mainValue + "Model) {");

			printWriter.println("\t\t" + model + " model = null;");

			if (fieldList.size() > 0) {

				printWriter.println("\t\tif (!helperExtension.isNullOrEmpty(dto)) {");

				printWriter.println("\t\t\tif (helperExtension.isNullOrEmpty(" + mainValue + "Model)) {");

				printWriter.println("\t\t\t\tmodel = new " + model + "();");
				printWriter.println("\t\t\t} else {");
				printWriter.println("\t\t\t\tmodel = " + mainValue + "Model;");
				printWriter.println("\t\t\t}");

				String id = "";

				String primaryKey = "";

				for (Field field : fieldList) {
					id = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);

					if (field.getName().toLowerCase().contains("Id".toLowerCase())) {

						primaryKey = id;

						printWriter.println("\t\t\tif (!helperExtension.isNullOrEmpty(dto.get" + id + "())) {");
						printWriter.println("\t\t\t\tmodel.set" + id + "(dto.get" + id + "());");

						printWriter.println("\t\t\t} else {");
//						"\"Hello\""
						printWriter.println("\t\t\t\tmodel.set" + id + "(\"ID_\" + helperExtension.getUniqueId());");
						printWriter.println("\t\t\t}");
//							if (!helperExtension.isNullOrEmpty(dto.getAddressId())) {
//							model.setAddressId(dto.getAddressId());
//						} else {
//							model.setAddressId("ADD_ID_" + helperExtension.getUniqueId());
//						}

					} else if (field.getType().getName().equalsIgnoreCase("java.util.Date")
							&& !listName.contains(field.getName())) {
						System.out.println("==$$$" + field.getName());
//						model.setStatus(getStatus(dto.getStatusDTO(), null));

						printWriter.println(
								"\t\t\tmodel.set" + id + "(helperExtension.timestampToDate(dto.get" + id + "()));");

//						printWriter
//								.println("\t\t\t\tmodel.set" + id + "(get" + id + "(dto.get" + id + "DTO(), null));");
					} else if (!list.contains(field.getType().getName())) {
						System.out.println("==" + field.getName());

						System.out.println("@@" + field.getType().getName());
//						model.setStatus(getStatus(dto.getStatusDTO(), null));

						printWriter.println("\t\t\tmodel.set" + id + "(get" + id + "(dto.get" + id + "DTO(), null));");
					} else if (!listName.contains(field.getName())) {
						System.out.println(field.getName());
//						model.setAddLine1(dto.getAddLine1());
						printWriter.println("\t\t\tmodel.set" + id + "(dto.get" + id + "());");
					}

				}

				printWriter.println("\t\t\tif (helperExtension.isNullOrEmpty(dto.get" + primaryKey + "())) {");
				printWriter.println("\t\t\t\tmodel.setCreatedOn(helperExtension.timestampToDate(dto.getCreatedOn()));");
				printWriter.println("\t\t\t\tmodel.setUpdatedOn(helperExtension.getDateTime());");
				printWriter.println("\t\t\t} else {");
				printWriter.println("\t\t\t\tmodel.setCreatedOn(helperExtension.getDateTime());");
				printWriter.println("\t\t\t\tmodel.setUpdatedOn(helperExtension.getDateTime());");
				printWriter.println("\t\t\t}");
				printWriter.println("\t\t\tmodel.setIsFlag(1);");

				printWriter.println("\t\t}");

			}

			printWriter.println("\t\treturn model;");

			printWriter.println("\t}");
			printWriter.println();

		}

//		public AddressDetails getAddressDetails(AddressDetailsDTO dto, AddressDetails addressDetailsModel) {
//		AddressDetails model = null;
//		if (!helperExtension.isNullOrEmpty(dto)) {
//			if (helperExtension.isNullOrEmpty(addressDetailsModel)) {
//				model = new AddressDetails();
//			} else {
//				model = addressDetailsModel;
//			}
//			if (!helperExtension.isNullOrEmpty(dto.getAddressId())) {
//				model.setAddressId(dto.getAddressId());
//			} else {
//				model.setAddressId("ADD_ID_" + helperExtension.getUniqueId());
//			}
//			model.setAddLine1(dto.getAddLine1());
//			model.setAddLine2(dto.getAddLine2());
//			model.setCity(dto.getCity());
//			model.setState(dto.getState());
//			model.setPincode(dto.getPincode());
//			if (helperExtension.isNullOrEmpty(dto.getAddressId())) {
//				model.setCreatedOn(helperExtension.timestampToDate(dto.getCreatedOn()));
//			} else {
//				model.setCreatedOn(helperExtension.timestampToDate(dto.getCreatedOn()));
//				model.setUpdatedOn(helperExtension.getDateTime());
//			}
//			model.setIsFlag(1);
//		}
//		return model;
//	}

	}

	public static boolean createDTOs(String filePath, String dtoFileName, List<Field> privateFieldName,
			String classpath) {

		List<Field> newFieldList = new ArrayList<>();

		Map<String, String> hash_map = new HashMap<>();

		List<String> list = new ArrayList<>();
		list.add("String");
		list.add("int");
		list.add("long");
		list.add("double");
		list.add("float");
		list.add("boolean");

		try {
			List<String> asd = new ArrayList<>();

			for (Field field : privateFieldName) {
				asd.add(field.getName());
			}

			// Create New Folders
			String dtoFolderPath = Test5.createNewFolders(mainFolderPath, filePath, "DTO");

			File f = new File(dtoFolderPath + "/" + dtoFileName + "DTO.java");

			if (!f.exists())
				f.createNewFile();

			List<String> dtoVariablesList = dtoHelper(asd);

			for (Field variable : privateFieldName) {
				for (String qwe : dtoVariablesList) {
					if (variable.getName().equalsIgnoreCase(qwe)) {
						newFieldList.add(variable);
					}
				}
			}

			PrintWriter printWriter = new PrintWriter(f);

			printWriter.println("package " + classpath.split("\\.")[0] + ".DTO;");
			printWriter.println();
			printWriter.println("public class " + dtoFileName + "DTO {");
			printWriter.println();

			// Start: import Statements
//			for()
			// End: import Statements

			for (Field poi : newFieldList) {
				String nameOFVariable = "";

				String asdqwe = poi.getType().getName();
				String jh[] = asdqwe.split("\\.");
				int index = jh.length;
				String asdfg = jh[index - 1];
				String convertedDataType = convertDataType(asdfg);
				if (list.contains(convertedDataType)) {
					nameOFVariable = poi.getName();
				} else if (convertedDataType.equalsIgnoreCase("List")) {

				} else {
					nameOFVariable = poi.getName() + "DTO";
					convertedDataType = convertedDataType + "DTO";
				}
				if (!convertedDataType.equalsIgnoreCase("List")) {
					printWriter.println("\t" + "private " + convertedDataType + " " + nameOFVariable + ";");
					hash_map.put(nameOFVariable, convertedDataType);
				}

			}

			printWriter.println();
			getterAndSetter(hash_map, printWriter);

			printWriter.println("}");
			printWriter.close();
		} catch (Exception e) {

		}

		return false;
	}

	private static void getterAndSetter(Map<String, String> hash_map, PrintWriter printWriter) {
		// TODO Auto-generated method stub

		for (Map.Entry<String, String> iterable_element : hash_map.entrySet()) {

			String key = iterable_element.getValue(); // For Data type
			String value = iterable_element.getKey(); // For Data Variable

			String mainValue = value.substring(0, 1).toUpperCase() + value.substring(1);

			printWriter.println("\t" + "public " + key + " " + "get" + mainValue + "() {");
			printWriter.println("\t\t" + "return " + value + ";");
			printWriter.println("\t}");
			printWriter.println();

			printWriter.println("\t" + "public void " + "set" + mainValue + "(" + key + " " + value + ") {");
			printWriter.println("\t\t" + "this." + value + " = " + value + ";");
			printWriter.println("\t}");
			printWriter.println();
		}

	}

	private static String convertDataType(String asdfg) {
		String returnValue = "";
		List<String> list = new ArrayList<>();
		list.add("String");
		list.add("int");
		list.add("long");
		list.add("double");
		list.add("float");
		list.add("boolean");
		if (list.contains(asdfg)) {
			returnValue = asdfg;
		} else if (asdfg.equalsIgnoreCase("date")) {
			returnValue = "long";
		} else {
			returnValue = asdfg;
		}
		return returnValue;
	}

	public static List<String> dtoHelper(List<String> privateFieldName) {
		String listRemove[] = { "isFlag" };
		for (String asd : listRemove) {
			privateFieldName.remove(asd);
		}
		return privateFieldName;
	}

	private static String createNewFolders(String mainFolderPath, String filePath, String folderName) {
		String a[] = filePath.split("/");
		String path = "/";
		for (int i = 0; i < a.length - 1; i++) {
			path = path + a[i] + "/";
		}
		String folderPath = path + folderName;
		folderPath = mainFolderPath + folderPath;
		File f = new File(folderPath);
		if (!f.exists())
			f.mkdir();
		return folderPath;
	}

}
