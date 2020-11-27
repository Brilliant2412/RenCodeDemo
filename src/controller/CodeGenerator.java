package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import model.ColumnProperty;
import model.TableInfo;


public class CodeGenerator {    
    static String url;
    static String pathString;
    static String pathOne;
    static String pathTwo;

    public CodeGenerator() {
        url="";
        pathString = "";
    }
    
    public CodeGenerator(String url,String path) {
        setUrl(url);
        setPathString(path);
    }

    public static String getPathString() {
        return pathString;
    }

    public static void setPathString(String pathString) {
        CodeGenerator.pathString = pathString;
    }

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        CodeGenerator.url = url;
    }
    

    
    static String capitalize(String s){
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    

    static String uncapitalize(String s){
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    static void genSELECT(FileWriter fileWriter, TableInfo tableInfo) throws IOException {
        fileWriter.append("\t\tsqlCommand.append(\" SELECT \");\n");
        for(int i = 0; i < tableInfo.columns.size(); i++){
            ColumnProperty colProp = tableInfo.columns.get(i);
            fileWriter.append("\t\tsqlCommand.append(\"");
            if(colProp.getColType().equals("Date")){
                fileWriter.append("to_char(tbl." + colProp.getColName() + ", 'DD/MM/YYYY') as " + colProp.getColName() + "ST");
                if(i != tableInfo.columns.size() - 1){
                    fileWriter.append(",");
                }
                fileWriter.append(" \");\n");
            }
            else {
                fileWriter.append("tbl." + colProp.getColName() + " as " + colProp.getColName());
                if(i != tableInfo.columns.size() - 1){
                    fileWriter.append(",");
                }
                fileWriter.append(" \");\n");
                if(!colProp.getFKTable().equals("")){
                    fileWriter.append("\t\tsqlCommand.append(\"");
                    fileWriter.append(colProp.getJoinTableName() + "." + colProp.getSelectField() + " as " + colProp.getColName() + "ST");
                    if(i != tableInfo.columns.size() - 1){
                        fileWriter.append(",");
                    }
                    fileWriter.append(" \");\n");
                }
            }
        }
        fileWriter.append("\n\t\tsqlCommand.append(\" FROM " + tableInfo.title  + " tbl \");\n");
        for(int i = 0; i < tableInfo.columns.size(); i++){
            ColumnProperty colProp = tableInfo.columns.get(i);
            if(!colProp.getFKTable().equals("")){
                fileWriter.append("\t\tsqlCommand.append(\" left join " +
                        colProp.getFKTable() + " " + colProp.getJoinTableName() + " on " + colProp.getJoinTableName() + "." +
                        colProp.getJoinField() + " = tbl." + colProp.getColName());
                if(!colProp.getJoinConstraint().equals("")){
                    fileWriter.append(" AND " + colProp.getJoinTableName() + "." + colProp.getJoinConstraint());
                }
                fileWriter.append("\");\n");
            }
        }
    }
    
     public static String checkDang(String tenTruong,String moTa,String kieuDL,String kieuNhap){
        String res = "";
        if (kieuDL.equals("String")){
            res = "\t\t\t\t<label class=\"col-lg-1 control-label  lb_input\">"+moTa+"</label>\n" +
                    "\t\t\t\t<div class=\"col-lg-2\">\n"+
                    "\t\t\t\t\t<input name=\""+tenTruong+"\" id=\""+tenTruong+"\" type=\"text\" class=\"form-control\"/>\n" +
                    "\t\t\t\t\t<span id=\""+tenTruong+"_error\" class=\"note note-error\"></span>\n" +
                    "\t\t\t\t</div>\n";
        }else if (kieuDL.equals("Long") || kieuDL.equals("Double")){
            if (kieuNhap.equals("Combobox")){
                res = "\t\t\t\t<label class=\"col-lg-1 control-label  lb_input\">"+moTa+"</label>\n" +
                        "\t\t\t\t<div class=\"col-lg-2\">\n" +
                        "\t\t\t\t\t<div id=\"cb"+tenTruong+"\"></div> \n" +
                        "\t\t\t\t\t<input name=\""+tenTruong+"\" id=\""+tenTruong+"\" class=\"text_hidden\"  />\n" +
                        "\t\t\t\t\t<span id=\""+tenTruong+"_error\" class=\"note note-error\"></span>\n" +
                        "\t\t\t\t</div>\n";
            }else{
                res = "\t\t\t\t<label class=\"col-lg-1 control-label  lb_input\">"+moTa+"</label>\n" +
                        "\t\t\t\t<div class=\"col-lg-2\">\n" +
                        "\t\t\t\t\t<input name=\""+tenTruong+"\" id=\""+tenTruong+"\" type=\"number\" class=\"form-control\"/>\n" +
                        "\t\t\t\t\t<span id=\""+tenTruong+"_error\" class=\"note note-error\"></span>                           \n" +
                        "\t\t\t\t</div>\n";
            }
        }else if (kieuDL.equals("Date")){
            res = "\t\t\t\t<label class=\"col-lg-1 control-label  lb_input\">"+moTa+"</label>\n" +
                    "\t\t\t\t<div class=\"col-md-2\" input-group>\n"+
                    "\t\t\t\t<input class=\"dateCalendar\" placeholder=\"Bắt đầu\" name=\""+tenTruong+"\" id=\""+tenTruong+"\" type=\"text\"/>\n" +
                    "\t\t\t\t\t<span id=\""+tenTruong+"_error\" class=\"note note-error\"></span>\n" +
                    "\t\t\t\t</div>\n";
        }
        return res;
    }

static void genBO(TableInfo tableInfo, String folder) throws IOException {
        FileWriter fileWriter = new FileWriter(folder + "\\" + tableInfo.tableName + "BO.java");
        fileWriter.write(
                "package com.tav.service.bo;\n\n" +

                    "import com.tav.service.base.db.dto.BaseFWDTOImpl;\n" +
                    "import com.tav.service.base.db.model.BaseFWModelImpl;\n" +
                    "import com.tav.service.dto." + tableInfo.tableName + "DTO;\n" +
                    "import com.vividsolutions.jts.geom.Geometry;\n" +
                    "import com.vividsolutions.jts.geom.Point;\n" +
                    "import java.util.Date;" +
                    "import javax.persistence.Column;\n" +
                    "import javax.persistence.Entity;\n" +
                    "import javax.persistence.GeneratedValue;\n" +
                    "import javax.persistence.Id;\n" +
                    "import javax.persistence.Table;\n" +
                    "import org.hibernate.annotations.GenericGenerator;\n" +
                    "import org.hibernate.annotations.Parameter;\n" +
                    "import org.hibernate.annotations.Type;\n\n" +

                    "@Entity\n" +
                    "@Table(name = \"" + tableInfo.title + "\")\n" +
                    "public class " + tableInfo.tableName + "BO extends BaseFWModelImpl {\n"
        );
        for(int i = 0; i < tableInfo.columns.size(); i++){
            fileWriter.append("\tprivate " + tableInfo.columns.get(i).getColType() + " " + tableInfo.columns.get(i).getColName() + ";\t\t//" + tableInfo.columns.get(i).getColDescription() + "\n");
        }
        String id = tableInfo.columns.get(0).getColName();
        fileWriter.append(
                "\n\tpublic " + tableInfo.tableName + "BO(){\n" +
                        "\t\tsetColId(\"" + id + "\");\n" +
                        "\t\tsetColName(\"" + id + "\");\n" +
                        "\t\tsetUniqueColumn(new String[]{\"" + id + "\"});\n" +
                        "\t}\n\n" +

                        "\t@Id\n" +
                        "\t@GeneratedValue(generator = \"sequence\")\n" +
                        "\t@GenericGenerator(name = \"sequence\", strategy = \"sequence\",\n" +
                        "\t\tparameters = {\n" +
                        "\t\t\t@Parameter(name = \"sequence\", value = \"" + tableInfo.title + "_seq\")\n" +
                        "\t\t}\n" +
                        "\t)\n\n"
        );
        for(int i = 0; i < tableInfo.columns.size(); i++){
            fileWriter.append(
                    "\t@Column(name = \"" + tableInfo.columns.get(i).getColName() + "\", length = " + tableInfo.columns.get(i).getLengthFormat() + ")\n" +
                            "\tpublic " + tableInfo.columns.get(i).getColType() + " get" + capitalize(tableInfo.columns.get(i).getColName()) + "(){\n" +
                            "\t\treturn " + tableInfo.columns.get(i).getColName() + ";\n" +
                            "\t}\n\n" +
                            "\tpublic void set" + capitalize(tableInfo.columns.get(i).getColName()) + "(" + tableInfo.columns.get(i).getColType() + " " + tableInfo.columns.get(i).getColName() + "){\n" +
                            "\t\tthis." + tableInfo.columns.get(i).getColName() + " = " + tableInfo.columns.get(i).getColName() + ";\n" +
                            "\t}\n\n"
            );
        }
        fileWriter.append(
                "\t@Override\n" +
                        "\tpublic BaseFWDTOImpl toDTO() {\n" +
                        "\t\t" + tableInfo.tableName + "DTO " + uncapitalize(tableInfo.tableName) + "DTO = new " + tableInfo.tableName + "DTO();\n"
        );
        for(int i = 0; i < tableInfo.columns.size(); i++){
            fileWriter.append("\t\t" + uncapitalize(tableInfo.tableName) + "DTO.set" + capitalize(tableInfo.columns.get(i).getColName()) + "(" + tableInfo.columns.get(i).getColName() + ");\n");
        }
        fileWriter.append(
                "\t\treturn " + uncapitalize(tableInfo.tableName) +"DTO;\n" +
                        "\t}\n" +
                        "}\n"
        );
        fileWriter.close();
    }

    static void genDTO(TableInfo tableInfo, String folder) throws IOException{
        FileWriter fileWriter = new FileWriter(folder + "\\" + tableInfo.tableName + "DTO.java");
        fileWriter.write(
                "package com.tav.service.dto;\n\n" +

                    "import com.tav.service.base.db.dto.BaseFWDTOImpl;\n" +
                    "import com.tav.service.bo." + tableInfo.tableName + "BO;\n" +
                    "import com.vividsolutions.jts.geom.Geometry;\n" +
                    "import com.vividsolutions.jts.geom.Point;\n" +
                    "import java.util.Date;" +
                    "import javax.xml.bind.annotation.XmlRootElement;\n\n" +

                    "@XmlRootElement(name = \"" + tableInfo.tableName + "DTO\")\n" +
                    "public class " + tableInfo.tableName + "DTO extends BaseFWDTOImpl<" + tableInfo.tableName + "BO> {\n"
        );
        for(int i = 0; i < tableInfo.columns.size(); i++){
            ColumnProperty colProp = tableInfo.columns.get(i);
            fileWriter.append("\tprivate " + colProp.getColType() + " " + colProp.getColName() + ";\t\t//" + colProp.getColDescription() + "\n");
            if(!colProp.getFKTable().equals("") || colProp.getColType().equals("Date")){
                fileWriter.append("\tprivate String " + colProp.getColName() + "ST;\n");
            }
        }
        fileWriter.append("\n");
        for(int i = 0; i < tableInfo.columns.size(); i++){
            ColumnProperty colProp = tableInfo.columns.get(i);
            fileWriter.append(
                    "\tpublic " + colProp.getColType() + " get" + capitalize(colProp.getColName()) + "(){\n" +
                            "\t\treturn " + colProp.getColName() + ";\n" +
                            "\t}\n\n" +

                            "\tpublic void set" + capitalize(colProp.getColName()) + "(" + colProp.getColType() + " " + colProp.getColName() + "){\n" +
                            "\t\tthis." + colProp.getColName() + " = " + colProp.getColName() + ";\n" +
                            "\t}\n\n"

            );
            if(!colProp.getFKTable().equals("") || colProp.getColType().equals("Date")){
                fileWriter.append(
                        "\tpublic String get" + capitalize(colProp.getColName()) + "ST(){\n" +
                                "\t\treturn " + colProp.getColName() + "ST;\n" +
                                "\t}\n\n" +

                                "\tpublic void set" + capitalize(colProp.getColName()) + "ST(String " + colProp.getColName() + "ST){\n" +
                                "\t\tthis." + colProp.getColName() + "ST = " + colProp.getColName() + "ST;\n" +
                                "\t}\n\n"
                );
            }
        }
        fileWriter.append("\n");
        fileWriter.append(
                "\t@Override\n" +
                        "\tpublic " + tableInfo.tableName +"BO toModel() {\n" +
                        "\t\t" + tableInfo.tableName + "BO " + uncapitalize(tableInfo.tableName) + "BO = new " + tableInfo.tableName + "BO();\n"
        );
        for(int i = 0; i < tableInfo.columns.size(); i++){
            fileWriter.append("\t\t" + uncapitalize(tableInfo.tableName) + "BO.set" + capitalize(tableInfo.columns.get(i).getColName()) + "(" + tableInfo.columns.get(i).getColName() + ");\n");
        }
        fileWriter.append(
                "\t\treturn " + uncapitalize(tableInfo.tableName) +"BO;\n" +
                        "\t}\n\n"

        );
        String id = tableInfo.columns.get(0).getColName();
        String idType = tableInfo.columns.get(0).getColType();
        fileWriter.append(
                "\t@Override\n" +
                        "\tpublic " + idType + " getFWModelId() {\n" +
                        "\t\treturn get" + capitalize(id) +"();\n" +
                        "\t}\n\n" +

                        "\t@Override\n" +
                        "\tpublic String catchName() {\n" +
                        "\t\treturn " + id +".toString();\n" +
                        "\t}\n" +
                        "}\n"
        );
        fileWriter.close();
    }

    static void genDAO(TableInfo tableInfo, String folder) throws IOException{
        FileWriter fileWriter = new FileWriter(folder + "\\" + tableInfo.tableName + "DAO.java");
        fileWriter.write(
                "package com.tav.service.dao;\n" +
                        "\n" +
                        "import com.tav.service.base.db.dao.BaseFWDAOImpl;\n" +
                        "import com.tav.service.bo." + tableInfo.tableName + "BO;\n" +
                        "import com.tav.service.dto." + tableInfo.tableName + "DTO;\n" +
                        "import com.tav.service.dto.ObjectCommonSearchDTO;\n" +
                        "import com.tav.service.dto.ServiceResult;\n" +
                        "import java.math.BigInteger;\n" +
                        "import java.text.SimpleDateFormat;\n" +
                        "import java.util.List;\n" +
                        "import java.util.Date;" +
                        "import org.hibernate.Criteria;\n" +
                        "import org.hibernate.HibernateException;\n" +
                        "import org.hibernate.Query;\n" +
                        "import org.hibernate.Session;\n" +
                        "import org.hibernate.exception.ConstraintViolationException;\n" +
                        "import org.hibernate.exception.JDBCConnectionException;\n" +
                        "import org.hibernate.transform.Transformers;\n" +
                        "import org.hibernate.type.LongType;\n" +
                        "import org.hibernate.type.StringType;\n" +
                        "import org.springframework.stereotype.Repository;\n" +
                        "import org.springframework.transaction.annotation.Transactional;\n" +
                        "\n" +
                        "@Repository(\"" + uncapitalize(tableInfo.tableName) + "DAO\")\n" +
                        "public class " + tableInfo.tableName + "DAO extends BaseFWDAOImpl<" + tableInfo.tableName + "BO, Long>{\n" +
                        "    \n");

        /***************************************************************************************
         *                                     getAll()
         ***************************************************************************************/
        fileWriter.append(
                "    public List<" + tableInfo.tableName + "DTO> getAll(ObjectCommonSearchDTO searchDTO, Integer offset, Integer limit) {\n" +
                        "        SimpleDateFormat formatter = new SimpleDateFormat(\"dd/MM/yyyy HH:mm:ss\");\n" +
                        "        StringBuilder sqlCommand = new StringBuilder();\n");
        genSELECT(fileWriter, tableInfo);
        fileWriter.append(
                "\n\t\tsqlCommand.append(\" WHERE 1=1 \");\n" +
                        "\t\tsqlCommand.append(\" ORDER BY tbl." + tableInfo.columns.get(0).getColName() + " \");" +

                        "\n\t\tQuery query = getSession().createSQLQuery(sqlCommand.toString())\n");
        for(int i = 0; i < tableInfo.columns.size(); i++){
            ColumnProperty colProp = tableInfo.columns.get(i);
            fileWriter.append("\t\t\t.addScalar(\"");
            if(colProp.getColType().equals("Date")){
                fileWriter.append(colProp.getColName() + "ST\", StringType.INSTANCE)\n");
            }
            else{
                fileWriter.append(colProp.getColName() + "\", " + capitalize((colProp.getColType())) + "Type.INSTANCE)\n");
                if(!colProp.getFKTable().equals("")){
                    fileWriter.append("\t\t\t.addScalar(\"");
                    fileWriter.append(colProp.getColName() + "ST\", StringType.INSTANCE)\n");
                }
            }
        }
        fileWriter.append(
                "\t\t\t.setResultTransformer(Transformers.aliasToBean(" + tableInfo.tableName + "DTO.class))\n" +
                        "\t\t\t.setFirstResult(offset);\n" +
                        "\t\tif (limit != null && limit != 0) {\n" +
                        "\t\t\tquery.setMaxResults(limit);\n" +
                        "\t\t}\n" +
                        "\t\treturn query.list();\n" +
                        "\t}\n\n");

        /***************************************************************************************
         *                                     GET COUNT
         ***************************************************************************************/
            fileWriter.append(
                    "public Integer getCount(ObjectCommonSearchDTO searchDTO) {\n" +
                    "        SimpleDateFormat formatter = new SimpleDateFormat(\"dd/MM/yyyy HH:mm:ss\");\n" +
                    "        StringBuilder sqlCommand = new StringBuilder();\n" +
                    "        sqlCommand.append(\" SELECT \");\n" +
                    "        sqlCommand.append(\" COUNT(1)\");\n" +
                    "        sqlCommand.append(\" FROM  " + tableInfo.title + " tbl \");\n" +
                    "        sqlCommand.append(\" WHERE 1=1 \");\n" +
                    "        Query query = getSession().createSQLQuery(sqlCommand.toString());\n" +
                    "        return ((BigInteger) query.uniqueResult()).intValue();\n" +
                    "    }"
            );

        /***************************************************************************************
         *                                     GET ONE
         ***************************************************************************************/
        fileWriter.append(
                "\t//get one\n" +
                        "\tpublic " + tableInfo.tableName + "DTO getOneObjById(Long id) {\n" +
                        "\t\tStringBuilder sqlCommand = new StringBuilder();\n");
        genSELECT(fileWriter, tableInfo);
        fileWriter.append("\t\tsqlCommand.append(\" WHERE tbl." + tableInfo.columns.get(0).getColName() +" = :" + tableInfo.columns.get(0).getColName() +"\");\n");
        fileWriter.append("\t\tQuery query = getSession().createSQLQuery(sqlCommand.toString())\n");
        for(int i = 0; i < tableInfo.columns.size(); i++){
            ColumnProperty colProp = tableInfo.columns.get(i);
            fileWriter.append("\t\t\t.addScalar(\"");
            if(colProp.getColType().equals("Date")){
                fileWriter.append(colProp.getColName() + "ST\", StringType.INSTANCE)\n");
            }
            else{
                fileWriter.append(colProp.getColName() + "\", " + capitalize((colProp.getColType())) + "Type.INSTANCE)\n");
                if(!colProp.getFKTable().equals("")){
                    fileWriter.append("\t\t\t.addScalar(\"");
                    fileWriter.append(colProp.getColName() + "ST\", StringType.INSTANCE)\n");
                }
            }
        }
        fileWriter.append(
                "\t\t\t.setResultTransformer(Transformers.aliasToBean(" + tableInfo.tableName + "DTO.class));\n" +
                        "\t\tquery.setParameter(\"" + tableInfo.columns.get(0).getColName()  + "\", id);\n" +
                        "\t\t" + tableInfo.tableName + "DTO item = (" + tableInfo.tableName + "DTO) query.uniqueResult();\n" +
                        "\t\treturn item;\n" +
                        "\t}\n\n");

        /***************************************************************************************
         *                                     DELETE
         ***************************************************************************************/
        fileWriter.append(
                "\t//delete\n" +
                        "\t@Transactional\n" +
                        "\tpublic ServiceResult deleteList(List<Long> listIds) {\n" +
                        "\t\tServiceResult result = new ServiceResult();\n" +
                        "\t\tQuery q = getSession().createQuery(\"DELETE FROM " + tableInfo.tableName + "BO WHERE " + tableInfo.columns.get(0).getColName() + " IN (:listIds)\");\n" +
                        "\t\tq.setParameterList(\"listIds\", listIds);\n" +
                        "\t\ttry {\n" +
                        "\t\t\tq.executeUpdate();\n" +
                        "\t\t} catch (ConstraintViolationException e) {\n" +
                        "\t\t\tlog.error(e);\n" +
                        "\t\t\tresult.setError(e.getMessage());\n" +
                        "\t\t\tresult.setErrorType(ConstraintViolationException.class.getSimpleName());\n" +
                        "\t\t\tresult.setConstraintName(e.getConstraintName());\n" +
                        "\t\t} catch (JDBCConnectionException e) {\n" +
                        "\t\t\tlog.error(e);\n" +
                        "\t\t\tresult.setError(e.getMessage());\n" +
                        "\t\t\tresult.setErrorType(JDBCConnectionException.class.getSimpleName());\n" +
                        "\t\t\t}\n" +
                        "\t\treturn result;\n" +
                        "\t}\n\n"
        );

        /***************************************************************************************
         *                                     UPDATE
         ***************************************************************************************/
        fileWriter.append(
                "\t//update\n" +
                    "\t@Transactional\n" +
                    "\tpublic ServiceResult updateObj(" + tableInfo.tableName + "DTO dto) {\n" +
                    "\t\tServiceResult result = new ServiceResult();\n" +
                    "\t\t" + tableInfo.tableName + "BO bo = dto.toModel();\n" +
                    "\t\ttry {\n" +
                    "\t\t\tgetSession().merge(bo);\n" +
                    "\t\t} catch (ConstraintViolationException e) {\n" +
                    "\t\t\tlog.error(e);\n" +
                    "\t\t\tresult.setError(e.getMessage());\n" +
                    "\t\t\tresult.setErrorType(ConstraintViolationException.class.getSimpleName());\n" +
                    "\t\t\tresult.setConstraintName(e.getConstraintName());\n" +
                    "\t\t} catch (JDBCConnectionException e) {\n" +
                    "\t\t\tlog.error(e);\n" +
                    "\t\t\tresult.setError(e.getMessage());\n" +
                    "\t\t\tresult.setErrorType(JDBCConnectionException.class.getSimpleName());\n" +
                    "\t\t} catch (HibernateException e) {\n" +
                    "\t\t\tlog.error(e);\n" +
                    "\t\t\tresult.setError(e.getMessage());\n" +
                    "\t\t}\n" +
                    "\t\treturn result;\n" +
                    "\t}\n\n"
        );

        /***************************************************************************************
         *                                     ADD
         ***************************************************************************************/
        fileWriter.append(
                "\t@Transactional\n" +
                    "\tpublic " + tableInfo.tableName + "BO addDTO(" + tableInfo.tableName + "DTO dto) {\n" +
                    "\t\tServiceResult result = new ServiceResult();\n" +
                    "\t\tSession session1 = getSession();\n" +
                    "\t\t" + tableInfo.tableName + "BO BO = new " + tableInfo.tableName + "BO();\n" +
                    "\t\ttry {\n" +
                    "\t\t\tBO = (" + tableInfo.tableName + "BO) session1.merge(dto.toModel());\n" +
                    "\t\t} catch (JDBCConnectionException e) {\n" +
                    "\t\t\tlog.error(e);\n" +
                    "\t\t\tresult.setError(e.getMessage());\n" +
                    "\t\t\tresult.setErrorType(JDBCConnectionException.class.getSimpleName());\n" +
                    "\t\t} catch (ConstraintViolationException e) {\n" +
                    "\t\t\tlog.error(e);\n" +
                    "\t\t\tresult.setError(e.getMessage());\n" +
                    "\t\t\tresult.setErrorType(ConstraintViolationException.class.getSimpleName());\n" +
                    "\t\t\tresult.setConstraintName(e.getConstraintName());\n" +
                    "\t\t} catch (HibernateException e) {\n" +
                    "\t\t\tlog.error(e);\n" +
                    "\t\t\tresult.setError(e.getMessage());\n" +
                    "\t\t}\n" +
                    "\t\treturn BO;\n" +
                    "\t}\n" +
                    "}"
        );
        fileWriter.close();
    }

    static void genBusiness(TableInfo tableInfo, String folder) throws IOException {
        FileWriter fileWriter = new FileWriter(folder + "\\" + tableInfo.tableName + "Business.java");
        fileWriter.write("package com.tav.service.business;\n" +
                "\n" +
                "public interface "+capitalize(tableInfo.tableName)+"Business {\n" +
                "    \n" +
                "}");
        fileWriter.close();
    }

    static void genBusinessImpl(TableInfo tableInfo, String folder) throws IOException{
        FileWriter fileWriter = new FileWriter(folder + "\\" + tableInfo.tableName + "BusinessImpl.java");
        fileWriter.write("package com.tav.service.business;\n" +
                "\n" +
                "import com.tav.service.base.db.business.BaseFWBusinessImpl;\n" +
                "import com.tav.service.bo."+ tableInfo.tableName +"BO;\n" +
                "import com.tav.service.common.Constants;\n" +
                "import com.tav.service.dao."+ tableInfo.tableName +"DAO;\n" +
                "import com.tav.service.dao.ObjectReationDAO;\n" +
                "import com.tav.service.dto."+ tableInfo.tableName + "DTO;\n" +
                "import com.tav.service.dto.ObjectCommonSearchDTO;\n" +
                "import com.tav.service.dto.ObjectSearchDTO;\n" +
                "import com.tav.service.dto.ServiceResult;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.List;\n" +
                "import java.util.Date;" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.context.annotation.Scope;\n" +
                "import org.springframework.context.annotation.ScopedProxyMode;\n" +
                "import org.springframework.stereotype.Service;\n");
        fileWriter.append("\n@Service(\""+uncapitalize(tableInfo.tableName)+"BusinessImpl\")"+
                "\n@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)\n");
        fileWriter.append("public class "+tableInfo.tableName+"BusinessImpl extends\n"+
                "\t\tBaseFWBusinessImpl<"+tableInfo.tableName+"DAO, "+ tableInfo.tableName+"DTO, "+tableInfo.tableName+"BO> implements "+tableInfo.tableName+"Business {\n"
        );
        fileWriter.append("\n\t@Autowired\n"+
                "\tprivate " +tableInfo.tableName+"DAO " + uncapitalize(tableInfo.tableName) +"DAO;\n"
        );
        fileWriter.append("\n\t@Override\n"+
                "\tpublic "+ tableInfo.tableName+"DAO" + " gettDAO() { return "+ uncapitalize(tableInfo.tableName) +"DAO; }\n"
        );
        fileWriter.append("\n\tpublic List<"+ tableInfo.tableName+"DTO>" + " getAll(ObjectCommonSearchDTO searchDTOTmp, Integer offset, Integer limit) {\n"+
                "\t\tList<"+ tableInfo.tableName+"DTO>" + " lstDTO = "+ uncapitalize(tableInfo.tableName) +"DAO"+".getAll(searchDTOTmp, offset, limit);\n" +
                "\t\treturn lstDTO;\n\t}\n"
        );
        fileWriter.append("\n\tpublic Integer getCount(ObjectCommonSearchDTO searchDTO) { return "+
                uncapitalize(tableInfo.tableName)+"DAO.getCount(searchDTO); }\n"
        );
        String gid = tableInfo.columns.get(0).getColName();
        String gidType = tableInfo.columns.get(0).getColType();
        fileWriter.append("\n\t//GET ONE\n\tpublic "+ tableInfo.tableName+"DTO getOneObjById("+gidType +" " +gid+") {\n"+
                "\t\t"+ tableInfo.tableName+"DTO dto = "+ uncapitalize(tableInfo.tableName) +"DAO"+".getOneObjById("+gid+");\n"+
                "\t\treturn dto;\n\t}\n"
        );
        fileWriter.append("\n\t//add\n"+
                "\tpublic ServiceResult addDTO("+ tableInfo.tableName+"DTO "+ uncapitalize(tableInfo.tableName) +"DTO) {\n"+
                "\t\t"+ tableInfo.tableName+"BO bo = "+ uncapitalize(tableInfo.tableName)+"DAO"+".addDTO("+ uncapitalize(tableInfo.tableName)+"DTO);\n"+
                "\t\tServiceResult serviceResult = new ServiceResult();\n"+
                "\t\tserviceResult.setId(bo.get"+capitalize(gid).trim()+"());\n"+
                "\t\treturn serviceResult;\n"+
                "\t}\n"
        );
        fileWriter.append("\n\t//update\n"+
                "\tpublic ServiceResult updateObj("+ tableInfo.tableName+"DTO "+ uncapitalize(tableInfo.tableName)+"DTO) {\n"+
                "\t\tServiceResult result;\n"+
                "\t\t"+ tableInfo.tableName+"BO bo = "+ uncapitalize(tableInfo.tableName)+"DAO"+".addDTO("+ uncapitalize(tableInfo.tableName)+"DTO);\n"+
                "\t\tresult = new ServiceResult();\n" +
                "\t\treturn result;\n"+
                "\t}\n"
        );
        fileWriter.append("\n\t//delete\n"+
                "\tpublic ServiceResult deleteList(ObjectCommonSearchDTO searchDTO) {\n"+
                "\t\tServiceResult result = "+ uncapitalize(tableInfo.tableName) +"DAO"+".deleteList(searchDTO.getLstFirst());\n"+
                "\t\treturn result;\n"+
                "\t}\n"
        );
        fileWriter.append("\n}");
        fileWriter.close();
    }

    static void genRsService(TableInfo tableInfo, String folder) throws IOException{
        FileWriter fileWriter = new FileWriter(folder + "\\" + tableInfo.tableName+"RsService.java");
        fileWriter.write("package com.tav.service.rest;\n" +
                "\n" +
                "import com.tav.service.dto."+ tableInfo.tableName+"DTO;\n" +
                "import com.tav.service.dto.ObjectCommonSearchDTO;\n" +
                "import javax.ws.rs.Consumes;\n" +
                "import javax.ws.rs.GET;\n" +
                "import javax.ws.rs.POST;\n" +
                "import javax.ws.rs.Path;\n" +
                "import javax.ws.rs.PathParam;\n" +
                "import javax.ws.rs.Produces;\n" +
                "import javax.ws.rs.core.MediaType;\n" +
                "import javax.ws.rs.core.Response;\n");
        fileWriter.append("\npublic interface "+ tableInfo.tableName+"RsService {\n");
        fileWriter.append("\t@POST\n" +
                "\t@Path(\"/getAll/{offset}/{limit}\")\n" +
                "\t@Consumes({MediaType.APPLICATION_JSON})\n" +
                "\t@Produces({MediaType.APPLICATION_JSON})\n" +
                "\tpublic Response getAll(ObjectCommonSearchDTO searchDTO, @PathParam(\"offset\") Integer offset, @PathParam(\"limit\") Integer limit);\n");

        fileWriter.append("\n\t@POST\n" +
                "\t@Path(\"/getCount\")\n" +
                "\t@Consumes({MediaType.APPLICATION_JSON})\n" +
                "\t@Produces({MediaType.APPLICATION_JSON})\n" +
                "\tpublic Response getCount(ObjectCommonSearchDTO searchDTO);\n");
        fileWriter.append("\n\t@GET\n" +
                "\t@Path(\"/getOneById/{id}\")\n" +
                "\t@Consumes({MediaType.APPLICATION_JSON})\n" +
                "\t@Produces({MediaType.APPLICATION_JSON})\n" +
                "\tpublic Response getOneById(@PathParam(\"id\") Long id);\n");
        fileWriter.append("\n\t@POST\n" +
                "\t@Path(\"/deleteList/\")\n" +
                "\t@Consumes({MediaType.APPLICATION_JSON})\n" +
                "\t@Produces({MediaType.APPLICATION_JSON})\n" +
                "\tpublic Response deleteList(ObjectCommonSearchDTO searchDTO);\n");
        fileWriter.append("\n\t@POST\n" +
                "\t@Path(\"/updateBO/\")\n" +
                "\t@Consumes({MediaType.APPLICATION_JSON})\n" +
                "\t@Produces({MediaType.APPLICATION_JSON})\n" +
                "\tpublic Response updateObj("+ tableInfo.tableName+"DTO "+uncapitalize(tableInfo.tableName)+"DTO);\n");
        fileWriter.append("\n\t@POST\n" +
                "\t@Path(\"/addDTO/\")\n" +
                "\t@Consumes({MediaType.APPLICATION_JSON})\n" +
                "\t@Produces({MediaType.APPLICATION_JSON})\n" +
                "\tpublic Response addDTO("+ tableInfo.tableName+"DTO "+uncapitalize(tableInfo.tableName)+"DTO);\n");
        fileWriter.append("}");
        fileWriter.close();
    }

    static void genRsServiceImpl(TableInfo tableInfo, String folder) throws IOException {
        FileWriter fileWriter = new FileWriter(folder + "\\" + tableInfo.tableName+"RsServiceImpl.java");
        fileWriter.write("package com.tav.service.rest;\n" +
                "\n" +
                "import com.tav.service.business."+ tableInfo.tableName+"BusinessImpl;\n" +
                "import com.tav.service.dto."+ tableInfo.tableName+"DTO;\n" +
                "import com.tav.service.dto.ObjectCommonSearchDTO;\n" +
                "import com.tav.service.dto.ServiceResult;\n" +
                "import java.util.List;\n" +
                "import java.util.Date;" +
                "import javax.ws.rs.core.Response;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n");
        fileWriter.append("\npublic class "+ tableInfo.tableName+"RsServiceImpl implements "+ tableInfo.tableName+"RsService{\n" +
                "\n" +
                "\t@Autowired\n" +
                "\tprivate "+ tableInfo.tableName+"BusinessImpl "+uncapitalize(tableInfo.tableName)+"BusinessImpl;\n");
        fileWriter.append("\n\t@Override\n" +
                "\tpublic Response getAll(ObjectCommonSearchDTO searchDTO, Integer offset, Integer limit) {\n" +
                "\t\tList<"+ tableInfo.tableName+"DTO> lst = "+uncapitalize(tableInfo.tableName)+"BusinessImpl.getAll(searchDTO, offset, limit);\n" +
                "\t\tif (lst == null) {\n" +
                "\t\t\treturn Response.status(Response.Status.BAD_REQUEST).build();\n" +
                "\t\t} else {\n" +
                "\t\t\treturn Response.ok(lst).build();\n" +
                "\t\t}\n" +
                "\t}\n");
        fileWriter.append("\n\t@Override\n" +
                "\tpublic Response getCount(ObjectCommonSearchDTO searchDTO) {\n" +
                "\t\tint result = "+uncapitalize(tableInfo.tableName)+"BusinessImpl.getCount(searchDTO);\n" +
                "\t\treturn Response.ok(result).build();\n" +
                "\t}\n");
        fileWriter.append("\n\t@Override\n" +
                "\tpublic Response getOneById(Long id) {\n" +
                "\t\t"+ tableInfo.tableName+"DTO result = "+uncapitalize(tableInfo.tableName)+"BusinessImpl.getOneObjById(id);\n" +
                "\t\treturn Response.ok(result).build();\n" +
                "\t}\n");
        fileWriter.append("\n\t@Override\n" +
                "\tpublic Response deleteList(ObjectCommonSearchDTO searchDTO) {\n" +
                "\t\tServiceResult result = "+uncapitalize(tableInfo.tableName)+"BusinessImpl.deleteList(searchDTO);\n" +
                "\t\tif (\"FAIL\".equals(result.getError())) {\n" +
                "\t\t\treturn Response.status(Response.Status.BAD_REQUEST).build();\n" +
                "\t\t} else {\n" +
                "\t\t\treturn Response.ok(result).build();\n" +
                "\t\t}\n" +
                "\t}\n");
        fileWriter.append("\n\t@Override\n" +
                "\tpublic Response updateObj("+ tableInfo.tableName+"DTO "+uncapitalize(tableInfo.tableName)+"DTO) {\n" +
                "\t\tServiceResult result = "+uncapitalize(tableInfo.tableName)+"BusinessImpl.updateObj("+uncapitalize(tableInfo.tableName)+"DTO);\n" +
                "\t\treturn Response.ok(result).build();\n" +
                "\t}\n");
        fileWriter.append("\n\t@Override\n" +
                "\tpublic Response addDTO("+ tableInfo.tableName+"DTO "+uncapitalize(tableInfo.tableName)+"DTO) {\n" +
                "\t\tServiceResult result = "+uncapitalize(tableInfo.tableName)+"BusinessImpl.addDTO("+uncapitalize(tableInfo.tableName)+"DTO);\n" +
                "\t\treturn Response.ok(result).build();\n" +
                "\t}\n");
        fileWriter.append("\n}");
        fileWriter.close();
    }

    static void genBean(TableInfo tableInfo, String folder) throws IOException {
        FileWriter fileWriter = new FileWriter(folder + "\\" + "bean.txt");
        fileWriter.append("<jaxrs:server id=\""+uncapitalize(tableInfo.tableName)+"RsServiceRest\"\naddress=\"/"+uncapitalize(tableInfo.tableName)+"RsServiceRest\">\n" +
                "\t\t<jaxrs:providers>\n" +
                "\t\t\t<ref bean=\"jsonProvider\" />\n" +
                "\t\t</jaxrs:providers>\n" +
                "\t\t<jaxrs:serviceBeans>\n" +
                "\t\t\t<bean id=\""+uncapitalize(tableInfo.tableName)+"RsServiceClass\"\nclass=\"com.tav.service.rest."+ tableInfo.tableName+"RsServiceImpl\"/>\n" +
                "\t\t</jaxrs:serviceBeans>\n" +
                "\t</jaxrs:server>");
        fileWriter.close();
    }

    static void genController(TableInfo tableInfo, String folder) throws IOException {
        FileWriter fileWriter = new FileWriter(folder + "\\" + tableInfo.tableName + "Controller.java");
        fileWriter.write("package com.tav.web.controller;\n" +
            "\n" +
            "import com.google.common.base.Strings;\n" +
            "import com.tav.web.common.DateUtil;" +
            "import com.google.gson.Gson;\n" +
            "import com.google.gson.JsonObject;\n" +
            "import com.tav.common.web.form.JsonDataGrid;\n" +
            "import com.tav.web.bo.ServiceResult;\n" +
            "import com.tav.web.bo.UserSession;\n" +
            "import com.tav.web.bo.ValidationResult;\n" +
            "import com.tav.web.common.CommonConstant;\n" +
            "import com.tav.web.common.CommonFunction;\n" +
            "import com.tav.web.common.ConvertData;\n" +
            "import com.tav.web.common.ErpConstants;\n" +
            "import com.tav.web.common.StringUtil;\n" +
            "import com.tav.web.data."+ tableInfo.tableName+"Data;\n" +
            "import com.tav.web.dto."+ tableInfo.tableName+"DTO;\n" +
            "import com.tav.web.dto.ImportErrorMessage;\n" +
            "import java.util.Date;" +
            "import com.tav.web.dto.ObjectCommonSearchDTO;\n" +
            "import java.io.BufferedOutputStream;\n" +
            "import java.io.File;\n" +
            "import java.io.FileInputStream;\n" +
            "import java.io.FileNotFoundException;\n" +
            "import java.io.FileOutputStream;\n" +
            "import java.io.IOException;\n" +
            "import java.nio.file.Files;\n" +
            "import java.nio.file.Path;\n" +
            "import java.nio.file.Paths;\n" +
            "import java.text.ParseException;\n" +
            "import java.text.SimpleDateFormat;\n" +
            "import java.util.ArrayList;\n" +
            "import java.util.HashMap;\n" +
            "import java.util.Iterator;\n" +
            "import java.util.List;\n" +
            "import java.util.regex.Pattern;\n" +
            "import javax.servlet.http.HttpServletRequest;\n" +
            "import javax.servlet.http.HttpServletResponse;\n" +
            "import javax.servlet.http.HttpSession;\n" +
            "import javax.ws.rs.core.MediaType;\n" +
            "import org.apache.poi.hssf.usermodel.HSSFWorkbook;\n" +
            "import org.apache.poi.ss.usermodel.Cell;\n" +
            "import org.apache.poi.ss.usermodel.DataFormatter;\n" +
            "import org.apache.poi.ss.usermodel.Row;\n" +
            "import org.apache.poi.ss.usermodel.Sheet;\n" +
            "import org.apache.poi.ss.usermodel.Workbook;\n" +
            "import org.apache.poi.xssf.usermodel.XSSFWorkbook;\n" +
            "import org.json.JSONObject;\n" +
            "import org.springframework.beans.factory.annotation.Autowired;\n" +
            "import org.springframework.stereotype.Controller;\n" +
            "import org.springframework.ui.Model;\n" +
            "import org.springframework.web.bind.annotation.ModelAttribute;\n" +
            "import org.springframework.web.bind.annotation.RequestMapping;\n" +
            "import org.springframework.web.bind.annotation.RequestMethod;\n" +
            "import org.springframework.web.bind.annotation.ResponseBody;\n" +
            "import org.springframework.web.multipart.MultipartFile;\n" +
            "import org.springframework.web.multipart.MultipartHttpServletRequest;\n" +
            "import org.springframework.web.servlet.ModelAndView;\n");

        fileWriter.append("\n" +
            "@Controller\n" +
            "public class "+ tableInfo.tableName+"Controller extends SubBaseController {\n" +
            "\n" +
            "    @Autowired\n" +
            "    private "+ tableInfo.tableName+"Data "+uncapitalize(tableInfo.tableName)+"Data;\n" +
            "\n" +
            "    @RequestMapping(\"/\" + ErpConstants.RequestMapping."+ tableInfo.title.toUpperCase()+")\n" +
            "    public ModelAndView agent(Model model, HttpServletRequest request) {\n" +
            "        return new ModelAndView(\""+uncapitalize(tableInfo.tableName)+"\");\n" +
            "    }\n" +
            "\n" +
            "    @RequestMapping(value = {\"/\" + ErpConstants.RequestMapping.GET_ALL_"+ tableInfo.title.toUpperCase()+"}, method = RequestMethod.GET)\n" +
            "    @ResponseBody\n" +
            "    public JsonDataGrid getAll(HttpServletRequest request) {\n" +
            "        try {\n" +
            "            // get info paging\n" +
            "            Integer currentPage = getCurrentPage();\n" +
            "            Integer limit = getTotalRecordPerPage();\n" +
            "            Integer offset = --currentPage * limit;\n" +
            "            JsonDataGrid dataGrid = new JsonDataGrid();\n" +
            "            ObjectCommonSearchDTO searchDTO = new ObjectCommonSearchDTO();\n" +
            "            List<"+ tableInfo.tableName+"DTO> lst = new ArrayList<>();\n" +
            "            Integer totalRecords = 0;\n" +
            "            totalRecords = "+uncapitalize(tableInfo.tableName)+"Data.getCount(searchDTO);\n" +
            "            if (totalRecords > 0) {\n" +
            "                lst = "+uncapitalize(tableInfo.tableName)+"Data.getAll(searchDTO, offset, limit);\n" +
            "            }\n" +
            "            dataGrid.setCurPage(getCurrentPage());\n" +
            "            dataGrid.setTotalRecords(totalRecords);\n" +
            "            dataGrid.setData(lst);\n" +
            "            return dataGrid;\n" +
            "        } catch (Exception e) {\n" +
            "            logger.error(e.getMessage(), e);\n" +
            "            return null;\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    @RequestMapping(value = \"/\" + ErpConstants.RequestMapping.GET_"+ tableInfo.title.toUpperCase()+"_BY_ID, method = RequestMethod.GET)\n" +
            "    public @ResponseBody\n" +
            "    "+ tableInfo.tableName+"DTO getOneById(HttpServletRequest request) {\n" +
            "        Long id = Long.parseLong(request.getParameter(\"gid\"));\n" +
            "        return "+uncapitalize(tableInfo.tableName)+"Data.getOneById(id);\n" +
            "    }\n" +
            "\n" +
            "    //add\n" +
            "    @RequestMapping(value = {\"/\" + ErpConstants.RequestMapping.ADD_"+ tableInfo.title.toUpperCase()+"}, method = RequestMethod.POST, produces = ErpConstants.LANGUAGE)\n" +
            "    @ResponseBody\n" +
            "    public String addOBJ(@ModelAttribute(\"" + uncapitalize(tableInfo.tableName) + "Form\") "+ tableInfo.tableName+"DTO "+uncapitalize(tableInfo.tableName)+"DTO, MultipartHttpServletRequest multipartRequest,\n" +
            "            HttpServletRequest request) throws ParseException {\n" +
            "\n" +
            "        JSONObject result;\n" +
            "        String error = validateForm("+uncapitalize(tableInfo.tableName)+"DTO);\n" +
            "        ServiceResult serviceResult;\n" +
            "        if (error != null) {\n" +
            "            return error;\n" +
            "        } else {\n");
        for(int i = 0; i < tableInfo.columns.size(); i++){
            ColumnProperty colProp = tableInfo.columns.get(i);
            if(colProp.getColType().equals("Date")){
                fileWriter.append(
                    "            if (!StringUtil.isEmpty(" + uncapitalize(tableInfo.tableName) + "DTO.get" + capitalize(colProp.getColName()) + "())) {\n" +
                    "                        " + uncapitalize(tableInfo.tableName) + "DTO.set" + capitalize(colProp.getColName()) + "(DateUtil.formatDate(" + uncapitalize(tableInfo.tableName) + "DTO.get" + capitalize(colProp.getColName()) + "()));\n" +
                    "            }\n"
                );
            }
        }
        fileWriter.append(
            "            serviceResult = "+uncapitalize(tableInfo.tableName)+"Data.addObj("+uncapitalize(tableInfo.tableName)+"DTO);\n" +
            "            processServiceResult(serviceResult);\n" +
            "            result = new JSONObject(serviceResult);\n" +
            "        }\n" +
            "        return result.toString();\n" +
            "    }\n" +
            "\n" +
            "    //update\n" +
            "    @RequestMapping(value = {\"/\" + ErpConstants.RequestMapping.UPDATE_"+ tableInfo.title.toUpperCase()+"}, method = RequestMethod.POST, produces = ErpConstants.LANGUAGE)\n" +
            "    @ResponseBody\n" +
            "    public String updateOBJ(@ModelAttribute(\"" + uncapitalize(tableInfo.tableName) + "Form\") "+ tableInfo.tableName+"DTO "+uncapitalize(tableInfo.tableName)+"DTO, MultipartHttpServletRequest multipartRequest,\n" +
            "            HttpServletRequest request) throws ParseException {\n" +
            "\n" +
            "        JSONObject result;\n" +
            "        String error = validateForm("+uncapitalize(tableInfo.tableName)+"DTO);\n" +
            "        ServiceResult serviceResult;\n" +
            "        if (error != null) {\n" +
            "            return error;\n" +
            "        } else {\n");
        for(int i = 0; i < tableInfo.columns.size(); i++){
            ColumnProperty colProp = tableInfo.columns.get(i);
            if(colProp.getColType().equals("Date")){
                fileWriter.append(
                    "            if (!StringUtil.isEmpty(" + uncapitalize(tableInfo.tableName) + "DTO.get" + capitalize(colProp.getColName()) + "())) {\n" +
                    "                        " + uncapitalize(tableInfo.tableName) + "DTO.set" + capitalize(colProp.getColName()) + "(DateUtil.formatDate(" + uncapitalize(tableInfo.tableName) + "DTO.get" + capitalize(colProp.getColName()) + "()));\n" +
                    "            }\n"
                );
            }
        }
        fileWriter.append(
            "            serviceResult = "+uncapitalize(tableInfo.tableName)+"Data.updateBO("+uncapitalize(tableInfo.tableName)+"DTO);\n" +
            "            processServiceResult(serviceResult);\n" +
            "            result = new JSONObject(serviceResult);\n" +
            "        }\n" +
            "        return result.toString();\n" +
            "    }\n" +
            "\n" +
            "    //validate\n" +
            "    private String validateForm("+ tableInfo.tableName+"DTO cbChaDTO) {\n" +
            "        List<ValidationResult> lsError = new ArrayList<>();\n" +
            "        if (lsError.size() > 0) {\n" +
            "            Gson gson = new Gson();\n" +
            "            return gson.toJson(lsError);\n" +
            "        }\n" +
            "        return null;\n" +
            "    }\n" +
            "\n" +
            "    @RequestMapping(value = {\"/\" + ErpConstants.RequestMapping.DELETE_"+ tableInfo.title.toUpperCase()+"}, method = RequestMethod.POST,\n" +
            "            produces = \"text/html;charset=utf-8\")\n" +
            "    public @ResponseBody\n" +
            "    String deleteObj(@ModelAttribute(\"objectCommonSearchDTO\") ObjectCommonSearchDTO objectCommonSearchDTO,\n" +
            "            HttpServletRequest request) {\n" +
            "        HttpSession session = request.getSession();\n" +
            "        ServiceResult serviceResult = "+uncapitalize(tableInfo.tableName)+"Data.deleteObj(objectCommonSearchDTO);\n" +
            "        processServiceResult(serviceResult);\n" +
            "        JSONObject result = new JSONObject(serviceResult);\n" +
            "        return result.toString();\n" +
            "    }\n" +
            "\n" +
            "}\n");

        fileWriter.close();
    }

    static void genControllerParameters(TableInfo tableInfo, String folder) throws IOException {
        FileWriter fileWriter = new FileWriter(folder + "\\" + tableInfo.tableName.toLowerCase() +".html");
        fileWriter.write("public static final String "+ tableInfo.title.toUpperCase()+" = \""+ tableInfo.tableName.toLowerCase() +".html\";\n" +
                "public static final String GET_"+ tableInfo.title.toUpperCase()+"_BY_ID = \"getone" + tableInfo.tableName.toLowerCase() +"bygid.json\";\n" +
                "public static final String GET_ALL_"+ tableInfo.title.toUpperCase()+"= \"getall"+ tableInfo.tableName.toLowerCase() +".json\";\n" +
                "public static final String ADD_"+ tableInfo.title.toUpperCase()+" = \"add"+ tableInfo.tableName.toLowerCase() +".html\";\n" +
                "public static final String UPDATE_"+ tableInfo.title.toUpperCase()+" = \"update"+ tableInfo.tableName.toLowerCase() +".html\";\n" +
                "public static final String DELETE_"+ tableInfo.title.toUpperCase()+" = \"delete"+ tableInfo.tableName.toLowerCase() +".html\";");
        fileWriter.close();
    }

    static void genData(TableInfo tableInfo, String folder) throws IOException {
        FileWriter fileWriter = new FileWriter(folder + "\\" + tableInfo.tableName + "Data.java");
        fileWriter.write("package com.tav.web.data;\n" +
                "\n" +
                "import com.tav.web.bo.ServiceResult;\n" +
                "import com.tav.web.common.Config;\n" +
                "import com.tav.web.common.RestRequest;\n" +
                "import com.tav.web.dto."+ tableInfo.tableName +"DTO;\n" +
                "import com.tav.web.dto.ObjectCommonSearchDTO;\n" +
                "import java.util.List;\n" +
                "import java.util.Date;" +
                "import org.apache.log4j.Logger;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.stereotype.Component;\n\n");
        fileWriter.append("@Component\n" +
                "public class "+ tableInfo.tableName +"Data {\n" +
                "\tprotected static final Logger logger = Logger.getLogger("+ tableInfo.tableName +"Data.class);\n" +
                "\tprivate static final String subUrl = \"/"+ uncapitalize(tableInfo.tableName) +"RsServiceRest\";\n");
        fileWriter.append("\n\t@Autowired\n" +
                "\tprivate Config config;\n");
        fileWriter.append("\n\t// get all\n" +
                "\tpublic List<"+ tableInfo.tableName +"DTO> getAll(ObjectCommonSearchDTO searchDTO, Integer offset, Integer limit) {\n" +
                "\t\tString url = config.getRestURL() + subUrl + \"/getAll/\" + offset + \"/\" + limit;\n" +
                "\t\ttry {\n" +
                "\t\t\tList<"+ tableInfo.tableName +"DTO> jsResult = RestRequest.postAndReturnObjectArray(url, searchDTO, "+ tableInfo.tableName +"DTO.class);\n" +
                "\t\t\tif (jsResult == null) {\n" +
                "\t\t\t\treturn null;\n" +
                "\t\t\t} else {\n" +
                "\t\t\t\treturn jsResult;\n" +
                "\t\t\t}\n" +
                "\t\t} catch (Exception e) {\n" +
                "\t\t\tlogger.error(e.getMessage(), e);\n" +
                "\t\t\treturn null;\n" +
                "\t\t}\n" +
                "\t}\n");
        fileWriter.append("\n\t//get count\n" +
                "\tpublic Integer getCount(ObjectCommonSearchDTO searchDTO) {\n" +
                "\t\tString url = config.getRestURL() + subUrl + \"/getCount\";\n" +
                "\t\treturn (Integer) RestRequest.postAndReturnObject(url, searchDTO, Integer.class);\n" +
                "\t}\n");
        fileWriter.append("\n\tpublic ServiceResult addObj("+ tableInfo.tableName +"DTO cbChaDTO) {\n" +
                "\t\tString url = config.getRestURL() + subUrl + \"/addDTO/\";\n" +
                "\t\tServiceResult result = (ServiceResult) RestRequest.postAndReturnObject(url, cbChaDTO, ServiceResult.class);\n" +
                "\t\treturn result;\n" +
                "\t}");
        fileWriter.append("\n\tpublic ServiceResult updateBO("+ tableInfo.tableName +"DTO cbChaDTO) {\n" +
                "\t\tString url = config.getRestURL() + subUrl + \"/updateBO/\";\n" +
                "\t\tServiceResult result = (ServiceResult) RestRequest.postAndReturnObject(url, cbChaDTO, ServiceResult.class);\n" +
                "\t\treturn result;\n" +
                "\t}\n");
        fileWriter.append("\n\tpublic ServiceResult deleteObj(ObjectCommonSearchDTO objectCommonSearchDTO) {\n" +
                "\t\tString url = config.getRestURL() + subUrl + \"/deleteList/\";\n" +
                "\t\tServiceResult result = (ServiceResult) RestRequest.postAndReturnObject(url, objectCommonSearchDTO, ServiceResult.class);\n" +
                "\t\treturn result;\n" +
                "\t}\n");
        fileWriter.append("\n\tpublic "+ tableInfo.tableName +"DTO getOneById(Long id) {\n" +
                "\t\tString url = config.getRestURL() + subUrl + \"/getOneById/\" + id;\n" +
                "\t\t"+ tableInfo.tableName +"DTO item = ("+ tableInfo.tableName +"DTO) RestRequest.getObject(url, "+ tableInfo.tableName +"DTO.class);\n" +
                "\t\treturn item;\n" +
                "\t}\n");
        fileWriter.append("\n\n}");
        fileWriter.close();
    }

    static void genTitle(TableInfo tableInfo, String folder) throws IOException {
        FileWriter fileWriter = new FileWriter(folder + "\\" + "title.txt");
        fileWriter.write("\t<definition name=\""+uncapitalize(tableInfo.tableName)+"\" extends=\"bodyContentAdmin.definition\">  \n" +
                "        \t<put-attribute name=\"title\" value=\"title.role\" />  \n" +
                "        \t<put-attribute name=\"body\" value=\"/WEB-INF/pages/"+ uncapitalize(tableInfo.tableName)+"/list.jsp\" />  \n" +
                "    \t</definition>");
        fileWriter.close();
    }

    static void genListjsp(TableInfo tableInfo, String folder) throws IOException {
        FileWriter fileWriter = new FileWriter(folder + "\\" + "List.jsp");
        fileWriter.write("<%@ page contentType=\"text/html;charset=UTF-8\" %>\n" +
                "<%@ taglib uri=\"http://www.springframework.org/tags/form\" prefix=\"form\"%>  \n" +
                "<%@ taglib uri=\"http://java.sun.com/jsp/jstl/fmt\" prefix=\"fmt\"%>\n" +
                "\n" +
                "<link href=\"${pageContext.request.contextPath}/share/bootstrap-multiselect/css/bootstrap-multiselect.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
                "<script src=\"${pageContext.request.contextPath}/share/bootstrap-multiselect/js/bootstrap-multiselect.js\" type=\"text/javascript\"></script>\n");
        fileWriter.append("<script src=\"${pageContext.request.contextPath}/share/core/js/"+uncapitalize(tableInfo.tableName)+".js\"/>\n");

        fileWriter.append("\n" +
                "\n" +
                "<link href=\"share/core/css/guideproperty.css\" rel=\"stylesheet\">\n" +
                "<link href=\"share/core/css/common.css\" rel=\"stylesheet\">\n" +
                "<jsp:include page=\"../common/header.jsp\"></jsp:include>\n" +
                "    <section id=\"widget-grid\" class=\"\" style=\"height:100%;\">\n" +
                "        <div class=\"row\">\n" +
                "            <article class=\"col-sm-12 col-md-12 col-lg-12\">\n" +
                "                <div style=\"height:100% !important;padding: 3px;\">\n" +
                "                    <article class=\"widgetTop1 col-sm-12 col-md-12 col-lg-12\">\n" +
                "                    <%--jsp:include page=\"formSearch.jsp\" /--%>\n" +
                "                    <div class=\"jarviswidget jarviswidget-color-blueDark\" id=\"wid-id-1\" data-widget-fullscreenbutton=\"false\"\n" +
                "                         data-widget-togglebutton=\"false\" data-widget-deletebutton=\"false\" data-widget-colorbutton=\"false\" data-widget-editbutton=\"false\"\n" +
                "                         style=\"height:100% !important;\">\n" +
                "                        <div style=\"height:100% !important;\">\n" +
                "                            <div class=\"widget-body no-padding\">\n" +
                "                                <div class=\"table-responsive\" id=\"table-responsive\">                           \n" +
                "                                    <table id=\"dataTblDocumentType\" style=\"width: 100%;\"></table>\n" +
                "                                    <jsp:include page=\"../common/tablePaging.jsp\"></jsp:include>\n" +
                "                                    </div>\n" +
                "                                </div>\n" +
                "                            </div>      \n" +
                "                        </div>\n" +
                "                    </article>\n" +
                "                </div>\n" +
                "            </article>\n" +
                "        <jsp:include page=\"dialogAdd.jsp\" /> \n" +
                "    </section>\n" +
                "    <input type=\"hidden\" id=\"screenId\" value=\"${requestScope['javax.servlet.forward.request_uri']}\"/>\n" +
                "<script src=\"${pageContext.request.contextPath}/share/core/js/common.js\"/>");
        fileWriter.close();
    }

    static void genJs(TableInfo tableInfo, String folder) throws  IOException{
        FileWriter fileWriter = new FileWriter(folder + "\\" + uncapitalize(tableInfo.tableName) + ".js");
        fileWriter.write("//$(\"#TBL_DOCUMENT_TYPE\").addClass(\"active\");\n" +
                "//$(\"#naviParent\").replaceWith($(\"#ROOT_LAND_POINT  span\").html());\n" +
                "//$(\"#naviChild\").replaceWith($(\"#cbma  span\").html());\n" +
                "\n\n");
        /*********************************************************************************************
         *                                 var editCellRendererVT
         *********************************************************************************************/
        fileWriter.append(
                "var editCellRendererVT = function (gid) {\n" +
                        "    return '<div style=\"text-align: center\">'\n" +
                        "            + '    <a class=\"tooltipCus iconEdit\" href=\"javascript:objTblDocumentType.editTblDocumentType(\\'' + gid + '\\')\">'\n" +
                        "            + '        <span class=\"tooltipCustext\">' + $(\"#tooltipEdit\").val() + '</span><img src=\"share/core/images/edit.png\" class=\"grid-icon\"/>'\n" +
                        "            + '    </a><a class=\"tooltipCus iconDelete\" href=\"javascript:objTblDocumentType.deleteTblDocumentType(\\'' + gid + '\\')\">'\n" +
                        "            + '        <span class=\"tooltipCustext\">' + $(\"#tooltipDelete\").val() + '</span><img src=\"share/core/images/delete_1.png\" class=\"grid-icon\"/></a>'\n" +
                        "            + '</div>';" +
                        "};\n\n");

        /*********************************************************************************************
         *                                 var datafields
         *********************************************************************************************/
        fileWriter.append("var datafields = [\n");
        for (int i = 0; i <tableInfo.columns.size(); i++) {
            ColumnProperty columnProperty = tableInfo.columns.get(i);
            if (columnProperty.getColType().equals("String")) {
                fileWriter.append("    {name: '" + columnProperty.getColName() + "', type: 'String'},\n");
            }
            else if (columnProperty.getColType().equals("Date")) {
                fileWriter.append("    {name: '" + columnProperty.getColName() + "', type: 'String'},\n");
                fileWriter.append("    {name: '" + columnProperty.getColName() + "ST', type: 'String'},\n");
            }
            else fileWriter.append("    {name: '" + columnProperty.getColName() + "', type: 'Number'},\n");
        }
        fileWriter.append("];\n\n");

        /*********************************************************************************************
         *                                 var columns
         *********************************************************************************************/
        fileWriter.append("var columns = [\n" +
                "\t{text: \"STT\", sortable: false, datafield: '', styleClass: 'stt', clstitle: 'tlb_class_center', res: \"data-hide='phone'\"},\n" +
                "\t{text: 'gid', datafield: 'gid', hidden: true},\n");
        for (int i = 0; i < tableInfo.columns.size(); i++) {
            ColumnProperty columnProperty = tableInfo.columns.get(i);

            if (columnProperty.isShow())
            {
                fileWriter.append("\t{text: \""+columnProperty.getColDescription()+"\", datafield: '"+columnProperty.getColName()+"', res: \"data-class='phone'\"},\n");

            }
        }
        fileWriter.append("\t{text: \"Chức năng\", datafield: 'gid', edit: 1, sortable: false, clstitle: 'tlb_class_center'}\n");
        fileWriter.append("];\n\n");

        /*********************************************************************************************
         *                                 var gridSetting
         *********************************************************************************************/
        fileWriter.append("var gridSetting = {\n" +
                "    sortable: false,\n" +
                "    virtualmode: true,\n" +
                "    isSetting: false,\n" +
                "    enableSearch: false,\n" +
                "    onClickRow: true\n" +
                "};\n\n");
        fileWriter.write(
        "doSearch = function () {\n" +
            "    vt_datagrid.loadPageAgainRes(\"#dataTblDocumentType\", \"getall" + tableInfo.tableName.toLowerCase() + ".json\");\n" +
            "    vt_sys.showBody();\n" +
            "    vt_loading.hideIconLoading();\n" +
            "    return false;\n" +
            "};\n\n"
        );
        for(int i = 0; i < tableInfo.columns.size(); i++){
            ColumnProperty colProp = tableInfo.columns.get(i);
            if(colProp.getColType().equals("Date")){
                fileWriter.append(
                    "$(\"#" + colProp.getColName() + "\").datepicker({\n" +
                        "\tduration: \"fast\",\n" +
                        "\tchangeMonth: true,\n" +
                        "\tchangeYear: true,\n" +
                        "\tdateFormat: 'dd/mm/yy',\n" +
                        "\tconstrainInput: true,\n" +
                        "\tdisabled: false,\n" +
                        "\tyearRange: \"-20:+10\",\n" +
                        "\tonSelect: function (selected) {\n" +
                        "\t}\n" +
                    "});\n\n"
                );
            }
        }
        fileWriter.append(
            "$(function () {\n" +
                "\tdoSearch();\n" +
                "\t\n" +

        /**********************************************************************************************
         *                            onClickBtAdd
         **********************************************************************************************/

                "\tonClickBtAdd = function () {\n" +
                "        vt_form.reset($('#" + uncapitalize(tableInfo.tableName) + "Form'));\n" +
                "        $(\"#gid\").val(\"\"); // reset form\n" +
                "        vt_form.clearError();\n" +
                "        $(\"#isedit\").val(\"0\");\n" +
                "        \n"
        );
        for(int i = 1; i < tableInfo.columns.size(); i++){
            ColumnProperty colProp = tableInfo.columns.get(i);
            if(colProp.getInputType().equals("Combobox")){
                fileWriter.append("\t\tvt_combobox.buildCombobox(\"cb" + colProp.getColName() + "\", \"" + colProp.getComboboxBuildPath() + "\", 0, \"" + colProp.getComboboxName() + "\", \"" + colProp.getComboboxValue() + "\", \"- Chọn " + colProp.getColDescription() + " -\", 0);\n");
            }
        }
        fileWriter.append("\n" +
                "        $('#dialog-formAddNew').dialog({\n" +
                "            title: \"Thêm mới " + tableInfo.description + "\"\n" +
                "        }).dialog('open');\n" +
                "        return false;\n" +
                "    };\n" +
                "\t\n"
        );

        /**********************************************************************************************
         *                            setValueToForm
         **********************************************************************************************/

        fileWriter.append(
                "\tsetValueToForm = function () {\n" +
                "        var item;\n");
        for(int i = 0; i < tableInfo.columns.size(); i++){
            ColumnProperty colProp = tableInfo.columns.get(i);
            if(colProp.getInputType().equals("Combobox")){
                fileWriter.append(
                    "\t\titem = $('#cb" + colProp.getColName() + "Combobox').val();\n" +
                    "\t\t$('input[name=\""  + colProp.getColName() + "\"]').val(item);\n"
                );
            }
        }
        fileWriter.append("\t}");

        /**********************************************************************************************
         *                            editTblDocumentTypeMethod
         **********************************************************************************************/

        fileWriter.append("\n" +
            "\teditTblDocumentTypeMethod = function () {\n" +
            "        clearError();\n" +
            "        setValueToForm();\n" +
            "        $.ajax({\n" +
            "            traditional: true,\n" +
            "            url: \"token.json\",\n" +
            "            dataType: \"text\",\n" +
            "            type: \"GET\"\n" +
            "        }).success(function (result) {\n" +
            "            if (vt_form.validate1(\"#" + uncapitalize(tableInfo.tableName) + "Form\", null, objTblDocumentType.validateRule))\n" +
            "            {\n" +
            "                var formdataTmp = new FormData();\n" +
            "                var formData = new FormData(document.getElementById(\"" + uncapitalize(tableInfo.tableName) + "Form\"));\n" +
            "                for (var pair of formData.entries()) {\n" +
            "                    formdataTmp.append(pair[0], pair[1]);\n" +
            "                }\n" +
            "                $.ajax({\n" +
            "                    async: false,\n" +
            "                    url: \"update" + tableInfo.tableName.toLowerCase() + ".html\",\n" +
            "                    data: formdataTmp,\n" +
            "                    processData: false,\n" +
            "                    contentType: false,\n" +
            "                    enctype: 'multipart/form-data',\n" +
            "                    type: \"POST\",\n" +
            "                    headers: {\"X-XSRF-TOKEN\": result},\n" +
            "                    dataType: 'json',\n" +
            "                    beforeSend: function (xhr) {\n" +
            "                        vt_loading.showIconLoading();\n" +
            "                    },\n" +
            "                    success: function (result) {\n" +
            "                        if (result.error != null) {\n" +
            "                            vt_loading.hideIconLoading();\n" +
            "                        } else\n" +
            "                        if (result !== null && result.length > 0) {\n" +
            "                            for (var i = 0; i < result.length; i++) {\n" +
            "                                $(\"#\" + result[i].fieldName + \"_error\").text(result[i].error);\n" +
            "                            }\n" +
            "                            setTimeout('$(\"#\"' + result[0].fieldName + ').focus()', 100);\n" +
            "                            vt_loading.hideIconLoading();\n" +
            "                        } else {\n" +
            "                            $(\"#dialog-formAddNew\").dialog(\"close\");\n" +
            "                            doSearch();\n" +
            "                            vt_loading.hideIconLoading();\n" +
            "                            vt_loading.showAlertSuccess(\"Chỉnh sửa thông tin thành công\");\n" +
            "                        }\n" +
            "\n" +
            "                    }, error: function (xhr, ajaxOption, throwErr) {\n" +
            "                        console.log(xhr);\n" +
            "                        console.log(ajaxOption);\n" +
            "                        console.log(throwErr);\n" +
            "                    }\n" +
            "                });\n" +
            "            }\n" +
            "        });\n" +
            "    };\n"
        );

        /**********************************************************************************************
         *                            addTblDocumentTypeMethod
         **********************************************************************************************/

        fileWriter.append("\n" +
            "\taddTblDocumentTypeMethod = function () {\n" +
            "        vt_form.clearError();\n" +
            "        setValueToForm();\n" +
            "        $.ajax({\n" +
            "            traditional: true,\n" +
            "            url: \"token.json\",\n" +
            "            dataType: \"text\",\n" +
            "            type: \"GET\"\n" +
            "        }).success(function (result) {\n" +
            "            if (vt_form.validate1(\"#" + uncapitalize(tableInfo.tableName) + "Form\", null, objTblDocumentType.validateRule))\n" +
            "            {\n" +
            "                var formdataTmp = new FormData();\n" +
            "                var formData = new FormData(document.getElementById(\"" + uncapitalize(tableInfo.tableName) + "Form\"));\n" +
            "                for (var pair of formData.entries()) {\n" +
            "                    formdataTmp.append(pair[0], pair[1]);\n" +
            "                }\n" +
            "                $.ajax({\n" +
            "                    url: \"add" + tableInfo.tableName.toLowerCase() + ".html\",\n" +
            "                    data: formdataTmp,\n" +
            "                    processData: false,\n" +
            "                    contentType: false,\n" +
            "                    enctype: 'multipart/form-data',\n" +
            "                    type: \"POST\",\n" +
            "                    headers: {\"X-XSRF-TOKEN\": result},\n" +
            "                    dataType: 'json',\n" +
            "                    beforeSend: function (xhr) {\n" +
            "                        vt_loading.showIconLoading();\n" +
            "                    },\n" +
            "                    success: function (result) {\n" +
            "                        if (result.error != null) {\n" +
            "                        } else if (result !== null && result.length > 0) {\n" +
            "                            for (var i = 0; i < result.length; i++) {\n" +
            "                                $(\"#\" + result[i].fieldName + \"_error\").text(result[i].error);\n" +
            "                            }\n" +
            "                            setTimeout('$(\"#' + result[0].fieldName + '\").focus()', 100);\n" +
            "                            vt_loading.hideIconLoading();\n" +
            "                        } else {\n" +
            "                            $(\"#dialog-formAddNew\").dialog(\"close\");\n" +
            "                            doSearch();\n" +
            "                            vt_loading.hideIconLoading();\n" +
            "                            vt_loading.showAlertSuccess(\"Thêm mới thành công\");\n" +
            "                        }\n" +
            "                    }, error: function (xhr, ajaxOption, throwErr) {\n" +
            "                        console.log(xhr);\n" +
            "                        console.log(ajaxOption);\n" +
            "                        console.log(throwErr);\n" +
            "                    }\n" +
            "                });\n" +
            "            }\n" +
            "\n" +
            "        });\n" +
            "    };\n" +
            "});\n\n"
        );

        /*********************************************************************************************
         *                                 var objTblDocumentType
         *********************************************************************************************/
        fileWriter.append("var objTblDocumentType = {\n" +
                /*********************************************************************************************
                 *                                 validateRule
                 *********************************************************************************************/
                "\tvalidateRule: {\n" +
                "        rules: {\n");
        for (int i = 0; i < tableInfo.columns.size(); i++) {
            ColumnProperty columnProperty = tableInfo.columns.get(i);
            if (columnProperty.isValidate())
            {
                fileWriter.append("\t\t\t"+columnProperty.getColName()+": {\n" +
                        "                required: true\n" +
                        "            }");
                if(i != tableInfo.columns.size() -1){
                    fileWriter.append(",");
                }
                fileWriter.append("\n");
            }

        }
        fileWriter.append("        },\n");

        fileWriter.append("        messages: {\n");

        for (int i = 0; i < tableInfo.columns.size(); i++) {
            ColumnProperty columnProperty = tableInfo.columns.get(i);
            if (columnProperty.isValidate())
            {
                fileWriter.append("\t\t\t"+columnProperty.getColName()+": {\n" +
                        "                required: \""+columnProperty.getValidateMessage()+"\"\n" +
                        "            }");
                if(i != tableInfo.columns.size() -1){
                    fileWriter.append(",");
                }
                fileWriter.append("\n");
            }

        }
        fileWriter.append("        }\n" +
                "    },\n\n");

                /*********************************************************************************************
                 *                                 editTblDocumentType
                 *********************************************************************************************/
        fileWriter.append("\teditTblDocumentType: function (id) {\n" +
                "        if (id !== null) {\n" +
                "            vt_form.reset($('#"+uncapitalize(tableInfo.tableName)+"Form'));\n" +
                "            vt_form.clearError();\n" +
                "            $.ajax({\n" +
                "                async: false,\n" +
                "                data: {gid: id},\n" +
                "                url: \"getone"+tableInfo.tableName.toLowerCase()+"bygid.json\",\n" +
                "                success: function (data, status, xhr) {\n" +
                "                    $(\"#gid\").val(data.gid);\n");
        for (int i = 1; i < tableInfo.columns.size(); i++) {
            ColumnProperty columnProperty = tableInfo.columns.get(i);
            if (columnProperty.getColType().equals("Date"))
            {
                fileWriter.append("\t\t\t\t\t$(\"#"+columnProperty.getColName()+"\").val(data."+columnProperty.getColName()+"ST);\n");
            }
            else if (columnProperty.getInputType().equals("Combobox"))
            {
                //ok gen lai cho anh file js cai file excel cua a la file nao a
                fileWriter.append("\t\t\t\t\tvt_combobox.buildCombobox(\"cb"+columnProperty.getColName()+"\", \""+columnProperty.getComboboxBuildPath()+"\", data."+columnProperty.getColName()+", \""+columnProperty.getComboboxName()+"\", \""+columnProperty.getComboboxValue()+"\", \"- Chọn "+columnProperty.getColDescription()+" -\", 0);\n");
            }
            else fileWriter.append("\t\t\t\t\t$(\"#"+columnProperty.getColName()+"\").val(data."+columnProperty.getColName()+");\n");

        }
        fileWriter.append("\n\t\t\t\t\t$('#dialog-formAddNew').dialog({\n" +
                "                        title: \"Cập nhật thông tin " + tableInfo.description + "\"\n" +
                "                    }).dialog('open');\n" +
                "                    // set css to form\n" +
                "                    $('#dialog-formAddNew').parent().addClass(\"dialogAddEdit\");\n" +
                "                    objCommon.setTimeout(\"code\");\n" +
                "                    return false;\n" +
                "                }\n" +
                "            });\n" +
                "        }\n" +
                "    },\n" +
                "\tgid: null,\n\n");
                /*********************************************************************************************
                 *                                 deleteTblDocumentType
                 *********************************************************************************************/
        fileWriter.append(
                "    deleteTblDocumentType: function (gid) {\n" +
                "        if (gid !== null) {\n" +
                "            var tmp_mess = '\"' + $(\"#dataTblDocumentType #\" + gid).parent().parent().parent().find(\".expand\").text() + '\"';\n" +
                "            vt_loading.showConfirmDeleteDialog(\"Bạn có chắc chắn muốn xóa danh mục\" + \" \" + tmp_mess, function (callback) {\n" +
                "                if (callback) {\n" +
                "                    objTblDocumentType.gid = gid;\n" +
                "                    objTblDocumentType.deleteOneTblDocumentType();\n" +
                "                }\n" +
                "            });\n" +
                "        }\n" +
                "    },\n\n");

                /*********************************************************************************************
                 *                                 deleteOneTblDocumentType
                 *********************************************************************************************/
        fileWriter.append(
                "\tdeleteOneTblDocumentType: function () {\n" +
                "        vt_loading.showIconLoading();\n" +
                "        var ids = objTblDocumentType.gid;\n" +
                "        var onDone = function (result) {\n" +
                "            if (result !== null && result.hasError) {\n" +
                "                $(\"#deleteDialogMessageError\").text(result.error);\n" +
                "                vt_loading.hideIconLoading();\n" +
                "            } else {\n" +
                "                $(\"#dialog-confirmDelete\").dialog(\"close\");\n" +
                "                if ($(\"#allValue\").val() === ids && pagenum > 1) {\n" +
                "                    pagenum--;\n" +
                "                }\n" +
                "                doSearch();\n" +
                "                vt_loading.hideIconLoading();\n" +
                "                vt_loading.showAlertSuccess(\"Xóa thành công\");\n" +
                "            }\n" +
                "        };\n" +
                "        vt_form.ajax(\"POST\", \"delete" + tableInfo.tableName.toLowerCase() + ".html\", {lstFirst: ids}, null, null, onDone);\n" +
                "    }\n" +
                "\n" +
                "};"
        );
        fileWriter.close();
    }

    static void genDialogAdd(TableInfo tableInfo, String folder) throws IOException {
        FileWriter fileWriter = new FileWriter(folder + "\\dialogAdd.jsp");
        fileWriter.write("<%@ page contentType=\"text/html;charset=UTF-8\" %>\n" +
                "<%@ taglib prefix=\"spring\" uri=\"http://www.springframework.org/tags\" %>\n" +
                "<%@ taglib uri=\"http://java.sun.com/jsp/jstl/core\" prefix=\"c\" %>\n" +
                "<%@ taglib uri=\"http://java.sun.com/jsp/jstl/functions\" prefix=\"fn\" %>\n" +
                "<%@ taglib uri=\"http://www.springframework.org/tags/form\" prefix=\"form\"%>  \n" +
                "<%@ taglib prefix=\"fmt\" uri=\"http://java.sun.com/jsp/jstl/fmt\" %>\n"+
                "<div id=\"dialog-formAddNew\">\n" +
                "\t<form:form id=\""+uncapitalize(tableInfo.tableName)+"Form\" modelAttribute=\""+uncapitalize(tableInfo.tableName)+"Form\" class=\"form-horizontal\">\t\n" +
                "\t\t<input type=\"hidden\" name=\"${_csrf.parameterName}\" value=\"${_csrf.token}\" />\n" +
                "\t\t<input type=\"hidden\" id=\"gid\" name=\"gid\" value=\"\"/>\n" +
                "\t\t<input type=\"hidden\" id=\"isedit\" name=\"isedit\" value=\"0\"/>\n" +
                "\t\t<fieldset>\n");
        fileWriter.append("\t\t\t<legend class=\"fs-legend-head\">\n" +
                "\t\t\t\t<span class=\"iconFS\"></span>\n" +
                "\t\t\t\t<span class=\"titleFS\" style=\"color: #047fcd !important;\"><b>Thông tin chung</b></span>\n" +
                "\t\t\t</legend>\n");
        int k =tableInfo.columns.size()-1;
        int r = k/4;
        int q = k%4;
        for (int i = 0;i<r;i++){
            fileWriter.append("\t\t\t<div class=\"form-group-add row\">\n");
            for (int j =0;j<=3;j++){
                String res = checkDang(tableInfo.columns.get(4*i+j+1).getColName(),
                        tableInfo.columns.get(4*i+j+1).getColDescription(),
                        tableInfo.columns.get(4*i+j+1).getColType(),
                        tableInfo.columns.get(4*i+j+1).getInputType()
                );
                fileWriter.append(res);
            }
            fileWriter.append("\t\t\t</div>\n\n");
        }
        if (q != 0){
            fileWriter.append("\t\t\t<div class=\"form-group-add row\">\n");
            for (int i = r*4+1;i<=k;i++){
                String res = checkDang(tableInfo.columns.get(i).getColName(),
                        tableInfo.columns.get(i).getColDescription(),
                        tableInfo.columns.get(i).getColType(),
                        tableInfo.columns.get(i).getInputType()
                );
                fileWriter.append(res);
            }
            fileWriter.append("\t\t\t</div>\n\n");
        }
        fileWriter.append("\t\t</fieldset>\n" +
            "\t</form:form>\n" +
            "</div>\n" +
            "<script type=\"text/javascript\">\n" +
            "\t$(\"#dialog-formAddNew\").dialog({\n" +
            "\t\twidth: isMobile.any() ? $(window).width() : ($(window).width() / 5 * 4),\n" +
            "\t\theight: $(window).height() / 5 * 4,\n" +
            "\t\tautoOpen: false,\n" +
            "\t\tmodal: true,\n" +
            "\t\tposition: [($(window).width() / 10 * 1), 50],\n" +
            "\t\topen: function () {\n" +
            "\t\t\t$('.areaTable').addClass('custom-overlay-popup-add-edit');\n" +
            "\t\t\t$('.dialogAddEdit').css('z-index', 1001);\n" +
            "\n" +
            "\t\t},\n" +
            "\t\tclose: function () {\n" +
            "\t\t\t$('.areaTable').removeClass('custom-overlay-popup-add-edit');\n" +
            "\n" +
            "\t\t},\n" +
            "\t\tbuttons: [{\n" +
            "\t\t\thtml: \"<fmt:message key='button.close' />\",\n" +
            "\t\t\t\"class\": \"btn btn-default\",\n" +
            "\t\t\tclick: function () {\n" +
            "\t\t\t$(this).dialog('close');\n" +
            "\t\t\t}\n" +
            " \t\t\t}, {\n" +
            "\t\t\t\thtml: \"<fmt:message key='button.update' />\",\n" +
            "\t\t\t\t\"class\": \"btn btn-primary\",\n" +
            "\t\t\t\t\"id\": \"btnAddTblInfoNotifyYes\",\n" +
            "\t\t\t\tclick: function () {\n" +
            "\t\t\t\t\tvar item = $('#isedit').val();\n" +
            "\t\t\t\t\tif (item === '0') {\n" +
            "\t\t\t\t\t\taddTblDocumentTypeMethod();\n" +
            "\t\t\t\t\t} else {\n" +
            "\t\t\t\t\t\teditTblDocumentTypeMethod();\n" +
            "\t\t\t\t\t}\n" +
            "\t\t\t\t}\n" +
            "\t\t\t}\n" +
            "\t\t]\n" +
            "\t});\n" +
            "</script>");
        fileWriter.close();
    }

    static void genDTO_Web(TableInfo tableInfo, String folder) throws IOException {
        FileWriter fileWriter = new FileWriter(folder + "\\" + tableInfo.tableName + "DTO.java");
        fileWriter.write(
        "package com.tav.web.dto;\n\n" +
 
            "public class " + tableInfo.tableName + "DTO{\n"
        );
        for(int i = 0; i < tableInfo.columns.size(); i++){
            ColumnProperty colProp = tableInfo.columns.get(i);
            //fileWriter.append("\tprivate " + colProp.getColType() + " " + colProp.getColName() + ";\t\t//" + colProp.getColDescription() + "\n");
            fileWriter.append("\tprivate ");
            if(colProp.getColType().equals("Date")){
                fileWriter.append("String " + colProp.getColName() + ";\t\t//" + colProp.getColDescription() + "\n");
            }
            else{
                fileWriter.append(colProp.getColType() + " " + colProp.getColName() + ";\t\t//" + colProp.getColDescription() + "\n");
            }
            if(!colProp.getFKTable().equals("") || colProp.getColType().equals("Date")){
                fileWriter.append("\tprivate String " + colProp.getColName() + "ST;\n");
            }
        }
        fileWriter.append("\n");
        for(int i = 0; i < tableInfo.columns.size(); i++){
            ColumnProperty colProp = tableInfo.columns.get(i);
            fileWriter.append(
                "\tpublic ");
            //colProp.getColType() +
            if(colProp.getColType().equals("Date")){
                fileWriter.append("String ");
            }
            else{
                fileWriter.append(colProp.getColType() + " ");
            }
            fileWriter.append("get"+capitalize(colProp.getColName()) + "(){\n" +
                "\t\treturn " + colProp.getColName() + ";\n" +
                "\t}\n\n");
 
            fileWriter.append(
                "\tpublic void set" + capitalize(colProp.getColName()) + "(");
            //colProp.getColType()
            if(colProp.getColType().equals("Date")){
                fileWriter.append("String ");
            }
            else{
                fileWriter.append(colProp.getColType() + " ");
            }
            fileWriter.append(" " + colProp.getColName() + "){\n" +
                    "\t\tthis." + colProp.getColName() + " = " + colProp.getColName() + ";\n" +
                    "\t}\n\n"
 
            );
            if(!colProp.getFKTable().equals("") || colProp.getColType().equals("Date")){
                fileWriter.append(
                        "\tpublic String get" + capitalize(colProp.getColName()) + "ST(){\n" +
                            "\t\treturn " + colProp.getColName() + "ST;\n" +
                            "\t}\n\n" +
 
                        "\tpublic void set" + capitalize(colProp.getColName()) + "ST(String " + colProp.getColName() + "ST){\n" +
                            "\t\tthis." + colProp.getColName() + "ST = " + colProp.getColName() + "ST;\n" +
                            "\t}\n\n"
                );
            }
        }
        fileWriter.append("}");
        fileWriter.close();
    }
    
    static void genService(TableInfo tableInfo, String folder) throws IOException {
        File dir = new File(folder);
        dir.mkdirs();
        genBO(tableInfo, folder);
        genDTO(tableInfo, folder);
        genDAO(tableInfo, folder);
        genBusiness(tableInfo, folder);
        genBusinessImpl(tableInfo, folder);
        genRsService(tableInfo, folder);
        genRsServiceImpl(tableInfo, folder);
        genBean(tableInfo, folder);
    }

    static void genWeb(TableInfo tableInfo, String folder) throws IOException {
        File dir = new File(folder);
        dir.mkdirs();
        File dir2 = new File(folder + "\\" + "webjsp");
        dir2.mkdirs();
        genControllerParameters(tableInfo, folder);
        genController(tableInfo, folder);
        genData(tableInfo, folder);
        genTitle(tableInfo, folder);
        genListjsp(tableInfo, dir2.getAbsolutePath());
        genJs(tableInfo, folder);
        genDialogAdd(tableInfo, dir2.getAbsolutePath());
        genDTO_Web(tableInfo, folder);
    }

    public static void GEN() {
        try {
            TableInfo tableInfo = new TableInfo(url);
            pathOne = pathString+"\\"+tableInfo.tableName+"\\"+"service";
            pathTwo = pathString+"\\"+tableInfo.tableName+"\\"+"web";
            genService(tableInfo,pathOne);
            genWeb(tableInfo, pathTwo);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
