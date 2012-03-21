package com.dianping.cat.report.page.model.spi.internal;

import java.util.List;

import com.dianping.cat.consumer.RealtimeConsumer;
import com.dianping.cat.consumer.transaction.TransactionAnalyzer;
import com.dianping.cat.consumer.transaction.model.entity.TransactionReport;
import com.dianping.cat.message.spi.MessageConsumer;
import com.dianping.cat.report.page.model.spi.ModelPeriod;
import com.dianping.cat.report.page.model.spi.ModelRequest;
import com.dianping.cat.report.page.model.spi.ModelResponse;
import com.dianping.cat.report.page.model.spi.ModelService;
import com.site.lookup.annotation.Inject;

public class BaseLocalModelService implements ModelService<TransactionReport> {
	@Inject(type = MessageConsumer.class, value = "realtime")
	private RealtimeConsumer m_consumer;

	@Override
	public ModelResponse<TransactionReport> invoke(ModelRequest request) {
		TransactionAnalyzer analyzer = getAnalyzer(request.getPeriod());
		ModelResponse<TransactionReport> response = new ModelResponse<TransactionReport>();

		if (analyzer != null) {
			List<String> domains = analyzer.getDomains();
			String d = request.getDomain();
			String domain = d != null && d.length() > 0 ? d : domains.isEmpty() ? null : domains.get(0);
			TransactionReport report = analyzer.getReport(domain);

			if (report != null) {
				report.getDomains().addAll(domains);
			}

			response.setModel(report);
		}

		return response;
	}

	private TransactionAnalyzer getAnalyzer(ModelPeriod period) {
		if (period.isCurrent() || period.isFuture()) {
			return (TransactionAnalyzer) m_consumer.getCurrentAnalyzer("transaction");
		} else if (period.isLast()) {
			return (TransactionAnalyzer) m_consumer.getLastAnalyzer("transaction");
		} else {
			throw new RuntimeException("Internal error: this method should not be called!");
		}
	}

	@Override
	public boolean isEligable(ModelRequest request) {
		ModelPeriod period = request.getPeriod();

		return !period.isHistorical();
	}
}
