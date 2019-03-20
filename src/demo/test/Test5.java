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

	static String mainFolderPath = "C:/test-wokspace/demo";
	static String classPath = "demo.models.";
	static String filePath = "src/demo/models";
	static String helperExtension = "HelperExtension helperExtension = new HelperExtension();";
	static String responserModelListPath = "import demo.test.ResponseModelList;";
	static String constantExtensionPath = "import demo.test.ConstantExtension;";
	static String helperExtensionPath = "import demo.test.HelperExtension;";

	static List<String> listOfModelClasses = new ArrayList<>();

	static String dtoFolderPkg = "";

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
		Boolean serviceFlag = createService(modelNameWithItsVariableNames);
		Boolean serviceImplFlag = createServiceImpl(modelNameWithItsVariableNames);
		Boolean controllerFlag = createController(modelNameWithItsVariableNames);
		Boolean falg4 = Test5.createDaoImpl(modelNameWithItsVariableNames);

	}

	private static Boolean createDaoImpl(Map<String, List<Field>> modelNameWithItsVariableNames) {
		try {

			for (Map.Entry<String, List<Field>> iterable_element : modelNameWithItsVariableNames.entrySet()) {

				String model = iterable_element.getKey(); // For model name
				List<Field> fieldList = iterable_element.getValue(); // For model fields List
				String param = " ";
				List<String> idFields = getParamFields(fieldList);
				for (String parameters : idFields) {
					param = "String " + parameters + ", " + param;
				}

				String daoPath = Test5.createNewFolders(mainFolderPath, filePath, "DaoImpl");
				// for (String model : listOfModelClasses) { // for model name

				File f = new File(daoPath + "/" + model + "DaoImpl.java");

				PrintWriter printWriter = new PrintWriter(f);
				printWriter.println("package " + Test5.classPath.split("\\.")[0] + ".DaoImpl;");
				printWriter.println();
				daoImplImports(printWriter);
				printWriter.println("import demo.test.DatabaseHelper;");
				printWriter.println("import demo.models." + model + ";");
				printWriter.println("import demo.test.BaseDaoImpl;");
				printWriter.println("import demo.Dao." + model + "Dao;");
				printWriter.println("\n@Repository");
				printWriter.println("public class " + model + "DaoImpl extends BaseDaoImpl<" + model + "> implements "
						+ model + "Dao {\n\n\t@PersistenceContext\n\t private EntityManager entityManager;\n\n\t"
						+ helperExtension + "\n\n\t @Override\n\t public List<" + model + "> get(String id, " + param
						+ "DatabaseHelper databaseHelper) { \n\t CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();\n\t CriteriaQuery<"
						+ model + "> criteriaQuery = criteriaBuilder.createQuery(" + model + ".class);\n\t Root<"
						+ model + "> root = criteriaQuery.from(" + model
						+ ".class);\n\t List<Predicate> predicateList = new ArrayList<Predicate>();\n\t if (!helperExtension.isNullOrEmpty(id)) {\r\n"
						+ "			predicateList.add(criteriaBuilder.equal(root.get(\"id\"), id));\r\n" + "		}");

				Map<String, String> hash_map = getParamKeyOrValue(fieldList);
				if (hash_map.size() > 0) {
					for (Map.Entry<String, String> ie : hash_map.entrySet()) {
						String key = ie.getKey();
						String value = ie.getValue();
						printWriter.println("\t if (!helperExtension.isNullOrEmpty(" + value + ")) {\r\n"
								+ "			predicateList.add(criteriaBuilder.equal(root.get(\"" + key + "\").get(\""
								+ value + "\")," + value + "));\r\n" + "		}");
					}
				}

				printWriter.println(
						"\t predicateList.add(criteriaBuilder.equal(root.get(\"isFlag\"), 1));\n\t if (!helperExtension.isNullOrEmpty(databaseHelper)) {\n\t\t// Search Starts\n\t\tif (!helperExtension.isNullOrEmpty(databaseHelper.getSearch())) {\n\t\t\tpredicateList.add(criteriaBuilder.like(root.get(\"\"), databaseHelper.getSearch() + \"%\"));\n\t\t}\n\t\t// Sorting Starts\r\n"
								+ "		if (databaseHelper.getSortOrder().equalsIgnoreCase(eOrderBy.enAsc.getKey())) {\r\n"
								+ "			criteriaQuery.orderBy(criteriaBuilder.asc(root.get(databaseHelper.getSortBy())));\r\n"
								+ "		} else {\r\n"
								+ "			criteriaQuery.orderBy(criteriaBuilder.desc(root.get(databaseHelper.getSortBy())));\r\n"
								+ "		}\r\n"
								+ "		criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));\r\n"
								+ "		// Pagination Starts\r\n"
								+ "		if (databaseHelper.getCurrentPage() != 0 && databaseHelper.getItemPerPage() != 0) {\r\n"
								+ "			final TypedQuery<" + model
								+ "> typedQuery = entityManager.createQuery(criteriaQuery);\r\n"
								+ "			typedQuery.setFirstResult((databaseHelper.getCurrentPage() - 1) * databaseHelper.getItemPerPage());\r\n"
								+ "			typedQuery.setMaxResults(databaseHelper.getItemPerPage());\r\n"
								+ "			return typedQuery.getResultList();\r\n" + "		}\r\n" + "	} else {\r\n"
								+ "			criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));\r\n"
								+ "		}\r\n"
								+ "		return entityManager.createQuery(criteriaQuery).getResultList();\r\n" + "	}");

				printWriter.println("\t@Override\r\n" + "	public List<" + model + "> exist(" + model
						+ "DTO dto) {\r\n"
						+ "		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();\r\n"
						+ "		CriteriaQuery<" + model + "> criteriaQuery = criteriaBuilder.createQuery(" + model
						+ ".class);\r\n" + "		Root<" + model + "> root = criteriaQuery.from(" + model
						+ ".class);\r\n" + "		List<Predicate> predicateList = new ArrayList<Predicate>();\r\n"
						+ "		if (!helperExtension.isNullOrEmpty(dto.getId())) {\r\n"
						+ "			predicateList.add(criteriaBuilder.notEqual(root.get(\"id\"), dto.getId()));\r\n"
						+ "		}\r\n" + "		predicateList.add(criteriaBuilder.equal(root.get(\"isFlag\"), 1));\r\n"
						+ "		criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));\r\n"
						+ "		return entityManager.createQuery(criteriaQuery).getResultList();\r\n" + "	}\r\n"
						+ "\r\n" + "}\r\n" + "");

				printWriter.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static void daoImplImports(PrintWriter printWriter) {
		printWriter.println(
				"import java.util.List;\nimport java.util.ArrayList;\nimport javax.persistence.EntityManager;\nimport javax.persistence.PersistenceContext;\nimport javax.persistence.TypedQuery;\nimport javax.persistence.criteria.CriteriaBuilder;\nimport javax.persistence.criteria.CriteriaQuery;\nimport javax.persistence.criteria.Predicate;\nimport javax.persistence.criteria.Root;\nimport org.springframework.stereotype.Repository;");
	}

	private static Map<String, String> getParamKeyOrValue(List<Field> fieldList) {
		Map<String, String> hash_map = new HashMap<>();
		List<String> list = new ArrayList<>();
		list.add("java.lang.String");
		list.add("java.lang.Integer");
		list.add("java.lang.Long");
		list.add("java.lang.Double");
		list.add("java.lang.Float");
		list.add("java.lang.Boolean");
		list.add("java.util.Date");
		list.add("long");

		for (Field field : fieldList) {

			if (!list.contains(field.getType().getName())) {
				hash_map.put(field.getType().getName(), field.getName().substring(0, 4) + "Id");
			}

		}

		return hash_map;
	}

	private static Boolean createController(Map<String, List<Field>> modelNameWithItsVariableNames) {
		try {
			for (Map.Entry<String, List<Field>> iterable_element : modelNameWithItsVariableNames.entrySet()) {

				String model = iterable_element.getKey(); // For model name
				List<Field> fieldList = iterable_element.getValue(); // For model fields List

				List<String> idFields = getParamFields(fieldList);
				String param2 = " ";

				for (String parameters : idFields) {
					param2 = parameters + "," + param2;
				}

				String param = " ";

				for (String parameters : idFields) {
					param = "@RequestHeader(value = " + "\"" + parameters + "\"" + ", defaultValue = \"\") String "
							+ parameters + ",\r\n " + param;
				}

				// Create a newFolder
				String daoPath = Test5.createNewFolders(mainFolderPath, filePath, "controller");
				// for (String model : listOfModelClasses) { // for model name

				// Create a file
				File f = new File(daoPath + "/" + model + "Service.java");

				PrintWriter printWriter = new PrintWriter(f);
				printWriter.println("package " + Test5.classPath.split("\\.")[0] + ".controller;");
				printWriter.println();
				printWriter.println("import java.util.List;");
				printWriter.println();

				printWriter.println("import org.springframework.beans.factory.annotation.Autowired;");
				printWriter.println("import org.springframework.web.bind.annotation.GetMapping;");
				printWriter.println("import org.springframework.web.bind.annotation.DeleteMapping;");
				printWriter.println("import org.springframework.web.bind.annotation.PostMapping;");
				printWriter.println("import org.springframework.web.bind.annotation.RequestBody;");
				printWriter.println("import org.springframework.web.bind.annotation.RequestHeader;");
				printWriter.println("import org.springframework.web.bind.annotation.RequestMapping;");
				printWriter.println("import org.springframework.web.bind.annotation.RestController;");

				printWriter.println(responserModelListPath);
				printWriter.println("import " + dtoFolderPkg + "DTO." + model + "DTO;");
				printWriter.println("import " + dtoFolderPkg + "Service." + model + "Service;");

				printWriter.println("@RestController\r\n" +

						"@RequestMapping(" + "\"" + model + "\"" + ")\r\n" + "public class " + model
						+ "Controller {\r\n" + "\r\n" + "	@Autowired\r\n" + "	private " + model
						+ "Service service;\r\n" + "\r\n" + "	@PostMapping(\"/createOrUpdate\")\r\n"
						+ "	public ResponseModelList<" + model + "DTO> createOrUpdate(@RequestBody " + model
						+ "DTO dto) {\r\n" + "		ResponseModelList<" + model
						+ "DTO> responseModel = service.createOrUpdate(dto);\r\n" + "		return responseModel;\r\n"
						+ "	}\r\n" + "\r\n" + "	@GetMapping({ \"/all\", \"/single\" })\r\n"
						+ "	public ResponseModelList<" + model
						+ "DTO> get(@RequestHeader(value = \"id\", defaultValue = \"\") String id,\r\n"

						+ param

						+ "			@RequestHeader(value = \"search\", defaultValue = \"\") String search,\r\n"
						+ "			@RequestHeader(value = \"currentPage\", defaultValue = \"0\") int currentPage,\r\n"
						+ "			@RequestHeader(value = \"itemPerPage\", defaultValue = \"0\") int itemPerPage,\r\n"
						+ "			@RequestHeader(value = \"sortBy\", defaultValue = \"\") String sortBy) {\r\n"
						+ "			@RequestHeader(value = \"sortOrder\", defaultValue = \"\") String sortOrder) {\r\n"
						+ "			DatabaseHelper databaseHelper = new DatabaseHelper(search, currentPage, itemPerPage, sortBy, sortOrder);\r\n"
						+ "			ResponseModelList<" + model + "DTO> responseModel = service.get(id, " + param2
						+ "databaseHelper);\r\n" + "			return responseModel;\r\n" + "	}\r\n" + "\r\n"
						+ "	@DeleteMapping\r\n" + "	public ResponseModelList<" + model
						+ "DTO> deleteByIds(@RequestHeader(\"ids\") List<String> ids) {\r\n"
						+ "		ResponseModelList<" + model + "DTO> responseModel = service.deleteByIds(ids);\r\n"
						+ "		return responseModel;\r\n" + "	}\r\n" + "}");

				printWriter.println();

				printWriter.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private static Boolean createService(Map<String, List<Field>> modelNameWithItsVariableNames) {
		try {

			for (Map.Entry<String, List<Field>> iterable_element : modelNameWithItsVariableNames.entrySet()) {

				String model = iterable_element.getKey(); // For model name
				List<Field> fieldList = iterable_element.getValue(); // For model fields List

				String param = " ";
				List<String> idFields = getParamFields(fieldList);
				for (String parameters : idFields) {
					param = " String " + parameters + ", " + param;
				}

				// Create a newFolder
				String daoPath = Test5.createNewFolders(mainFolderPath, filePath, "Service");
				// for (String model : listOfModelClasses) { // for model name

				// Create a file
				File f = new File(daoPath + "/" + model + "Service.java");

				PrintWriter printWriter = new PrintWriter(f);
				printWriter.println("package " + Test5.classPath.split("\\.")[0] + ".Service;");
				printWriter.println();
				printWriter.println("import java.util.List;");
				printWriter.println();
				printWriter.println(responserModelListPath);
				printWriter.println("import " + classPath + model + ";");
				printWriter.println("import " + dtoFolderPkg + "DTO." + model + "DTO;");

				printWriter.println();

				printWriter.println("public interface " + model + "Service {\r\n" + "\r\n" + "\tResponseModelList<"
						+ model + "DTO> createOrUpdate(" + model + "DTO dto);\r\n" + "\r\n" + "\tResponseModelList<"
						+ model + "DTO> get(String id," + param + "DatabaseHelper databaseHelper);\r\n" + "\r\n"
						+ "\tResponseModelList<" + model + "DTO> deleteByIds(List<String> ids);\r\n" + "\r\n" + "}");

				printWriter.close();

			}

//			ResponseModelList<AclRoleDTO> createOrUpdate(AclRoleDTO dto);
//
//			ResponseModelList<AclRoleDTO> get(String roleId, String userId, String roleTypeId, String groupId, String statusId,
//					String sectionId, DatabaseHelper databaseHelper);
//
//			ResponseModelList<AclRoleDTO> deleteByIds(List<String> ids);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private static Boolean createServiceImpl(Map<String, List<Field>> modelNameWithItsVariableNames) {
		try {

			for (Map.Entry<String, List<Field>> iterable_element : modelNameWithItsVariableNames.entrySet()) {

				String model = iterable_element.getKey(); // For model name
				List<Field> fieldList = iterable_element.getValue(); // For model fields List

				String param = " ";
				List<String> idFields = getParamFields(fieldList);
				for (String parameters : idFields) {
					param = parameters + "," + param;
				}

				String param2 = " ";

				for (String parameters : idFields) {
					param2 = " String " + parameters + "," + param2;
				}

				// Create a newFolder
				String daoPath = Test5.createNewFolders(mainFolderPath, filePath, "ServiceImpl");
				// for (String model : listOfModelClasses) { // for model name

				// Create a file
				File f = new File(daoPath + "/" + model + "ServiceImpl.java");

				PrintWriter printWriter = new PrintWriter(f);

				// import start form here
				printWriter.println("package " + Test5.classPath.split("\\.")[0] + ".ServiceImpl;");
				printWriter.println();
				printWriter.println("import java.util.List;");
				printWriter.println("import java.util.ArrayList;");
				printWriter.println("import org.apache.log4j.Logger;");
				printWriter.println("import org.springframework.beans.factory.annotation.Autowired;");
				printWriter.println("import org.springframework.stereotype.Service;");
				printWriter.println("import org.springframework.transaction.annotation.Transactional;");
				printWriter.println(responserModelListPath);
				printWriter.println(constantExtensionPath);
				printWriter.println(helperExtensionPath);
				printWriter.println();
				printWriter.println("import " + dtoFolderPkg + "Dao." + model + "Dao;");
				printWriter.println("import " + dtoFolderPkg + "models." + model + ";");
				printWriter.println("import " + dtoFolderPkg + "DTO." + model + "DTO;");
				printWriter.println("import " + dtoFolderPkg + "Service." + model + "Service;");
				printWriter.println("import " + dtoFolderPkg + "ModelAndDTOSetterExtension.ModelSetterExtension;");
				printWriter.println("import " + dtoFolderPkg + "ModelAndDTOSetterExtension.DTOSetterExtension;");
				printWriter.println();
				// import end form here

				// methods start from here

				/*
				 * createOrUpdate() method start from here
				 */
				printWriter.println("@Service\r\n" + "@Transactional(readOnly = true)\r\n" + "public class " + model
						+ "ServiceImpl implements " + model + "Service {\r\n" + "\r\n" + "	@Autowired\r\n"
						+ "	private " + model + "Dao dao;\r\n" + "\r\n"
						+ "	final static Logger logger = Logger.getLogger(" + model + "ServiceImpl.class);\r\n"
						+ "	ResponseModelList<" + model + "DTO> responseModel = new ResponseModelList<>();\r\n"
						+ "	HelperExtension helperExtension = new HelperExtension();\r\n" + "\r\n"
						+ "	private boolean status = false;\r\n" + "	private String message = \"\";\r\n"
						+ "	private List<" + model + "DTO> list = null;\r\n" + "\r\n" + "\r\n" + "	@Override\r\n"
						+ "	@Transactional(readOnly = false)\r\n" + "	public ResponseModelList<" + model
						+ "DTO> createOrUpdate(" + model + "DTO dto) {\r\n" + "		list = new ArrayList<>();\r\n"
						+ "		try {\r\n" + "			List<" + model + "> models = dao.exists(dto);\r\n"
						+ "			if (models.size() > 0) {\r\n"
						+ "				putValueInResponseModel(false, ConstantExtension.SAME_NAME_IN_CLASS, null, null);\r\n"
						+ "			} else {\r\n"
						+ "				if (!helperExtension.isNullOrEmpty(dto.getId())) {\r\n"
						+ "					putValueInResponseModel(true, ConstantExtension.CLASS_UPDATED, dto, null);\r\n"
						+ "\r\n" + "				} else {\r\n"
						+ "					putValueInResponseModel(true, ConstantExtension.CLASS_ADDED, dto, null);\r\n"
						+ "				}\r\n" + "			}\r\n" + "			responseModel = new ResponseModelList<"
						+ model + "DTO>(status, message, list);\r\n" + "		} catch (Exception e) {\r\n"
						+ "			e.printStackTrace();\r\n" + "			responseModel = new ResponseModelList<"
						+ model + "DTO>(status, message, list);\r\n" + "		}\r\n"
						+ "		return responseModel;\r\n" + "	}\r\n" + "\r\n"
						+ "	// This method is used to put the value in Global Variables(status, message,\r\n"
						+ "	// list\r\n" + "	// of particular dto)\r\n"
						+ "	public void putValueInResponseModel(boolean status, String message, " + model + "DTO dto, "
						+ model + " classesModel) {\r\n" + "		this.status = status;\r\n"
						+ "		this.message = message;\r\n" + "		List<" + model
						+ "DTO> dtos = new ArrayList<>();\r\n" + "		if (!helperExtension.isNullOrEmpty(dto)) {\r\n"
						+ "			" + model + " model = new ModelSetterExtension().get" + model
						+ "(dto, classesModel);\r\n" + "			dao.saveOrUpdate(model);\r\n"
						+ "			dto.setId(model.getId());\r\n" + "			dtos.add(dto);\r\n"
						+ "			this.list = dtos;\r\n" + "		}\r\n" + "	}\r\n" + "\r\n" + "	@Override\r\n"
						+ "	public ResponseModelList<" + model + "DTO> get(String id," + param2
						+ "DatabaseHelper databaseHelper) {\r\n" + "		list = new ArrayList<>();\r\n"
						+ "		int numberOfPages = 0;\r\n" + "		try {\r\n" + "			List<" + model
						+ "> daoList = dao.get(id, " + param + "databaseHelper);\r\n"
						+ "			Integer count[] = null;\r\n"
						+ "			if (databaseHelper.getCurrentPage() != 0 && databaseHelper.getItemPerPage() != 0) {\r\n"
						+ "				DatabaseHelper tempDatabasehelper = new DatabaseHelper(databaseHelper);\r\n"
						+ "				int items = dao.get(id, " + param + "tempDatabasehelper).size();\r\n"
						+ "				count = new HelperExtension().pagination(databaseHelper, items);\r\n"
						+ "			}\r\n" + "			for (" + model + " model : daoList) {\r\n" + "				"
						+ model + "DTO dto = new DTOSetterExtension().get" + model + "DTO(model);\r\n"
						+ "				list.add(dto);\r\n" + "			}\r\n"
						+ "			responseModel = new ResponseModelList<" + model
						+ "DTO>(true, ConstantExtension.SUCCESS_RECEIVE, list, count,\r\n"
						+ "					numberOfPages);\r\n" + "		} catch (Exception e) {\r\n"
						+ "			e.printStackTrace();\r\n" + "			responseModel = new ResponseModelList<"
						+ model + "DTO>(status, message, list);\r\n" + "		}\r\n"
						+ "		return responseModel;\r\n" + "	}\r\n" + "\r\n" + "	@Override\r\n"
						+ "	@Transactional(readOnly = false)\r\n" + "	public ResponseModelList<" + model
						+ "DTO> deleteByIds(List<String> ids) {\r\n" + "		list = new ArrayList<>();\r\n"
						+ "		try {\r\n" + "			for (String id : ids) {\r\n" + "				" + model
						+ " model = dao.findById(id);\r\n" + "				model.setIsFlag(0);\r\n"
						+ "				dao.saveOrUpdate(model);\r\n"
						+ "				list.add(new DTOSetterExtension().get" + model + "DTO(model));\r\n"
						+ "			}\r\n" + "			responseModel = new ResponseModelList<" + model
						+ "DTO>(true, ConstantExtension.SUCCESS_MESSAGE_DELETED, list);\r\n"
						+ "		} catch (Exception exception) {\r\n" + "			exception.printStackTrace();\r\n"
						+ "			responseModel = new ResponseModelList<" + model
						+ "DTO>(false, ConstantExtension.MESSAGE_ERROR, list);\r\n" + "		}\r\n"
						+ "		return responseModel;\r\n" + "	}\r\n" + "	\r\n" + "}");
				/*
				 * createOrUpdate() method end from here
				 */

				// methods end from here

//				printWriter.println("}");
				printWriter.close();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
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

				// Create a newFolder
				String daoPath = Test5.createNewFolders(mainFolderPath, filePath, "Dao");
				// for (String model : listOfModelClasses) { // for model name

				// Create a file
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

			File f = new File(ModelAndDTOSetterExtensionPath + "/DTOSetterExtension.java");

			PrintWriter printWriter = new PrintWriter(f);
			printWriter.println("package " + Test5.classPath.split("\\.")[0] + ".ModelAndDTOSetterExtension;");
			printWriter.println();
			createModelSetterExtensionImports(listOfModelClasses, printWriter);
			printWriter.println();
			printWriter.println("public class DTOSetterExtension {");
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
			printWriter.println("public class ModelSetterExtension {");
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

		dtoFolderPkg = "";
		// Start : setting the package name of dto before the file name and set that
		// name into the "dtoFolderPkg" variable
		String asdf[] = Test5.classPath.split("\\.");
		for (int i = 0; i < asdf.length - 1; i++)
			dtoFolderPkg = dtoFolderPkg + asdf[i] + ".";
		// End : setting the package name of dto before the file name and set that name
		// into the "dtoFolderPkg" variable

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
