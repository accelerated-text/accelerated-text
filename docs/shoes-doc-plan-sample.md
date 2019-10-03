## Sample text

The Nike Air Max 95 Premium provides exceptional support and comfort with a sleek update on a classic design. Its premium lacing results in a snug fit for everyday wear.

## Accelerated Text structure

### Alternative 1

```yaml
- Segment:
	text-type: description
	items:
		- Product:
		    name:   { Attribute: Name }             # Nike Aire Max 95 Premium
			purposes:
                -   relationship:   provides
                    value:
                        All:
                            - Attribute:    Main Feature        # "comfort"
                            - Attribute:    Secondary Feature   # "support"
                -   relationship:   provides
                    value:
                        Attribute:  Style           # "sleek update on a classic design"
        - Component:
            name:   { Attribute: Lacing }           # "premium"
            purposes:
                -   relationship:   results in
                    value:
                        If:
                            predicate:
                                Equals:
                                    { Attribute: Lacing }
                                    "premium"
                            then:
                                Any-of:
                                    - Quote:    "snug fit for everyday wear"
                                    - Quote:    "never gets into a knot"
                                    - Quote:    "remains firmly tied"
```

### Alternative 2

```yaml
- Segment:
	text-type: description
	items:
		- Product:
		    name:   { Attribute: Name }             # Nike Aire Max 95 Premium
            relationships:
                - Sequence:
                    - Provides:
                        - Attribute:    Main Feature        # "comfort"
                        - Attribute:    Secondary Feature   # "support"
                    - Elaborate:
                        - Attribute:    Style               # "sleek update on a classic design"
        - Component:
            name:   { Attribute: Lacing }                   # "premium"
            relationships:
                - Consequence:
                    - If:
                        predicate:
                            Equals:
                                { Attribute: Lacing }
                                "premium"
                        then:
                            Any-of:
                                - Quote:    "snug fit for everyday wear"
                                - Quote:    "never gets into a knot"
                                - Quote:    "remains firmly tied"
```
