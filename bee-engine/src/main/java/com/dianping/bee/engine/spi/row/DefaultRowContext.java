package com.dianping.bee.engine.spi.row;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dianping.bee.engine.spi.meta.ColumnMeta;

public class DefaultRowContext implements RowContext {
	private ColumnMeta[] m_columns;

	private Object[] m_values;

	private RowListener m_listener;

	private Map<String, List<Object>> m_attributes;

	public DefaultRowContext(ColumnMeta[] columns) {
		m_columns = columns;
		m_values = new Object[columns.length];
	}

	@Override
	public void apply() {
		m_listener.onRow(this);
		Arrays.fill(m_values, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getAttributeValues(String name) {
		return (List<T>) m_attributes.get(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ColumnMeta> T getColumn(int colIndex) {
		return (T) m_columns[colIndex];
	}

	@Override
	public int getColumnSize() {
		return m_columns.length;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getFirstAttribute(String name, T defaultValue) {
		List<T> list = (List<T>) m_attributes.get(name);

		if (list == null || list.isEmpty()) {
			return defaultValue;
		} else {
			return list.get(0);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getFirstAttribute(int attrIndex, T defaultValue) {
		int index = 0;
		Collection<List<Object>> values = m_attributes.values();
		Iterator<List<Object>> iterator = values.iterator();
		while (iterator.hasNext() && index++ < attrIndex) {
			iterator.next();
		}
		if (iterator.hasNext()) {
			List<T> list = (List<T>) iterator.next();

			if (list == null || list.isEmpty()) {
				return defaultValue;
			} else {
				return list.get(0);
			}
		}
		return defaultValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getValue(int colIndex) {
		return (T) m_values[colIndex];
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getValue(String columnName) {
		int len = m_columns.length;

		for (int i = 0; i < len; i++) {
			ColumnMeta column = m_columns[i];

			if (column.getName().equalsIgnoreCase(columnName)) {
				return (T) m_values[i];
			}
		}

		return null;
	}

	public void setAttributes(Map<String, List<Object>> attributes) {
		m_attributes = attributes;
	}

	@Override
	public void setColumnValue(int colIndex, Object value) {
		m_values[colIndex] = value;
	}

	public void setRowListener(RowListener listener) {
		m_listener = listener;
	}
}
