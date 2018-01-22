/**
 * 簡単なリレーショナルデータベース
 */

import java.util.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class RelationalDatabase {
	public static void main(String args[]) {
		// "表1"
		Relation relation1 = new Relation("表1", "名前", "国籍", "性別", "職業", "年齢");
		relation1.insert("大介", "日本", "男", "学生", "23");
		relation1.insert("クリス", "イギリス", "男", "学生", "26");
		relation1.insert("ルーシー", "アメリカ", "女", "公務員", "34");
		relation1.insert("ジョン", "アメリカ", "男", "会社員", "43");
		relation1.insert("ヨハン", "ドイツ", "男", "学生", "23");
		relation1.insert("陽子", "日本", "女", "主婦", "31");
		relation1.show("表1", 20, 20);
		// "表3"
		Relation relation3 = new Relation("表3", "名前", "母国語");
		relation3.insert("クリス", "英語");
		relation3.insert("ジョン", "英語");
		relation3.insert("ヨハン", "ドイツ語");
		relation3.show("表3", 40, 40);
		// "表2 SELECT 職業=学生 and 性別=男"
		Set<Constraint> constraints = new HashSet<Constraint>();
		constraints.add(new EqualityConstraint("職業", "学生"));
		constraints.add(new EqualityConstraint("性別", "男"));
		Relation relation2 = relation1.select(constraints);
		relation2.name = "表2";
		relation2.show("表2 SELECT 職業=学生 and 性別=男", 60, 60);
		// "表4 JOIN 表２and 表３ with 名前＝名前"
		Relation relation4 = relation2.join(relation3, "名前");
		relation4.name = "表4";
		relation4.show("表4 JOIN 表２and 表３ with 名前＝名前", 80, 80);
		// "表5 PROJECT over 国籍 and 母国語"
		Relation relation5 = relation4.project("表2.国籍", "表3.母国語");
		relation5.name = "表5";
		relation5.show("表5 PROJECT over 国籍 and 母国語", 100, 100);
	}
}

// リレーション（関係）
class Relation {
	String name; // リレーション名
	String fields[]; // フィールド名一覧
	List<String[]> records; // レコード
	Map<String, Integer> index; // 何番目のフィールドかを管理

	// コンストラクタ
	public Relation(String name, String... fields) {
		this.name = name;
		this.fields = fields;
		records = new ArrayList<String[]>();
		index = new HashMap<String, Integer>();
		int i = 0;
		for (String fieled: fields)
			index.put(fieled, i++);
	}

	// コンストラクタ(内部利用)
	Relation(Relation relation, List<String[]> records, boolean deepCopy) {
		name = relation.name;
		this.fields = relation.fields.clone();
		this.index = new HashMap<String, Integer>(relation.index);
		this.records = deepCopy ? deepCopy(records) : records; 
	}
	
	// select演算: 指定した条件を満たすレコードを返す
	public Relation select(Set<Constraint> constraints) {
		// 指定した条件を満たすレコードからリレーションを生成する
		List<String[]> feasibleRecords = solve(records, constraints);
		return new Relation(this, feasibleRecords, false);
	}

	// project演算: 指定したフィールドを取り出す
	public Relation project(String... fields) {
		// 新しいリレーションを作成する
		Relation relation = new Relation(name, fields);
		for (String[] record: records) {
			// 指定したフィールドだけのレコードを作成
			String[] values = new String[fields.length];
			int i = 0;
			for (String field: fields)
				values[i++] = record[index.get(field)];
			relation.insert(values);
		}
		return relation;
	}

	// join演算: リレーションを結合する(inner join)
	public Relation join(Relation relation, String field) {
		// フィールドの結合
		int fieldSize = fields.length + relation.fields.length;
		String[] joinedFields = new String[fieldSize];
		int i = 0;
		for (String field1: fields)
			joinedFields[i++] = name + "." + field1;
		for (String field1: relation.fields)
			joinedFields[i++] = relation.name + "." + field1;

		// レコードの結合
		int index1 = index.get(field);
		int index2 = relation.index.get(field);
		List<String[]> joinedRecords = new ArrayList<String[]>();
		for (String[] record1: records) {
			String value1 = record1[index1];
			for (String[] record2: relation.records) {
				String value2 = record2[index2];
				if (value1.equals(value2)) {
					// レコードを結合する
					String[] record = new String[fieldSize];
					int j = 0;
					for (String value: record1)
						record[j++] = value;
					for (String value: record2)
						record[j++] = value;
					joinedRecords.add(record);
				}
			}
		}
		// 新しいリレーションの生成
		String joinedName = name + "+" + relation.name;
		relation = new Relation(joinedName, joinedFields);
		relation.records = joinedRecords;

		return relation;
	}

	// レコードを追加する
	public void insert(String... values) {
		// レコードをコピーする
		String record[] = new String[values.length];
		int i = 0;
		for (String s: values)
			record[i++] = s;
		// リレーションに追加する
		records.add(record);
	}

	// レコードを更新する
	public void update(String field, String value, Set<Constraint> constraints) {
		// 条件を満たすレコードを得る
		List<String[]> records1 = solve(records, constraints);
		// 条件を満たすレコードを更新する
		int i = index.get(field);
		for (String[] record: records1)
			record[i] = value;
	}
	
	// レコードを削除する
	public void delete(Set<Constraint> constraints) {
		// 条件を満たすレコードを得る
		List<String[]> records1 = solve(records, constraints);
		// 条件を満たすレコードを消す
		Iterator<String[]> i = records.iterator();
		while (i.hasNext()) {
			String[] record = i.next();
			if (records1.contains(record))
				i.remove();
		}
	}

	// レコードのリストをディープコピーする
	List<String[]> deepCopy(List<String[]> src) {
		List<String[]> copy = new ArrayList<String[]>();
		for (String[] record: src) {
			record = record.clone();
			copy.add(record);
		}
		return copy;
	}
	
	// 条件を満たすレコードを選ぶ
	List<String[]> solve(List<String[]> allRecords, Set<Constraint> constraints) {
		List<String[]> feasibleRecords = new ArrayList<String[]>(allRecords);
		// 条件を満たさないレコードを削除する
		for (Constraint constraint: constraints) {
			Iterator<String[]> j = feasibleRecords.iterator();
			while (j.hasNext()) {
				if (!constraint.isSatisfied(this, j.next()))
					j.remove();
			}
		}
		return feasibleRecords;
	}

	// リレーションを表示する
	public void show(String title, int x, int y) {
		int w = 100 * fields.length;
		int h = 16 * records.size() + 20;
		String[][] tableData = new String[records.size()][fields.length];
		for (int i = 0; i < records.size(); i++)
			for (int j = 0; j < fields.length; j++)
				tableData[i][j] = records.get(i)[j];
		JFrame frame = new JFrame();
		frame.setTitle(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTable table = new JTable(tableData, fields);
		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(w, h));
		JPanel p = new JPanel();
		p.add(sp);
		frame.getContentPane().add(p, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.setBounds(x, y, table.getWidth() + 32, h + 40);
	}
}

// selectなどで使う制約条件
abstract class Constraint {
	abstract boolean isSatisfied(Relation relation, String[] record);
}

// 等しいかどうかをチェックする制約条件
class EqualityConstraint extends Constraint {	
	String field;
	String value;
	
	EqualityConstraint(String field, String value) {
		this.field = field;
		this.value = value;
	}

	boolean isSatisfied(Relation relation, String[] record) {
		int index = relation.index.get(field);
		return value.equals(record[index]);
	}
}
