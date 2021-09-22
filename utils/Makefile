print-document-plan:
	clojure -M:dp print-plan $(dp)

export-generated-text:
	clojure -M:generate $(dp) $(data_file) $(output_path) $(language)

export-all-document-plans:
	clojure -M:dp export-plans $(dir)
