package io.virtue.core.manager;

import io.virtue.common.util.StringUtil;
import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.MatchScope;
import io.virtue.core.Virtue;
import io.virtue.core.config.RegistryConfig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RegistryConfig Manager.
 */
public class RegistryConfigManager extends AbstractRuleManager<RegistryConfig> {

    public RegistryConfigManager(Virtue virtue) {
        super(virtue);
    }

    /**
     * Get global configs.
     *
     * @return
     */
    public List<RegistryConfig> globalConfigs() {
        return getManagerMap().values().stream().
                filter(RegistryConfig::global)
                .collect(Collectors.toList());
    }

    /**
     * Register config.
     *
     * @param registryConfig
     */
    public void register(RegistryConfig registryConfig) {
        String name = StringUtil.isBlankOrDefault(registryConfig.name(), registryConfig.type());
        register(name, registryConfig);
    }

    /**
     * Add to matched caller by protocol rules.
     *
     * @param config
     * @param rules
     */
    public void addProtocolRule(RegistryConfig config, String... rules) {
        addProtocolRule(config, MatchScope.CALLER, rules);
    }

    /**
     * Add to matched caller by path rules.
     *
     * @param config
     * @param rules
     */
    public void addPathRule(RegistryConfig config, String... rules) {
        addPathRule(config, MatchScope.CALLER, rules);
    }

    @Override
    public void addProtocolRule(RegistryConfig config, MatchScope scope, String... rules) {
        RuleWrapper<RegistryConfig> wrapper = ruleMap.computeIfAbsent(config, RuleWrapper::new);
        if (scope == MatchScope.CALLER) {
            wrapper.addProtocolRulesForCaller(rules);
        }
    }

    @Override
    public void addPathRule(RegistryConfig config, MatchScope scope, String... rules) {
        RuleWrapper<RegistryConfig> wrapper = ruleMap.computeIfAbsent(config, RuleWrapper::new);
        if (scope == MatchScope.CALLER) {
            wrapper.addPathRulesForCaller(rules);
        }
    }

    @Override
    public void executeRules() {
        List<Caller<?>> allCaller = virtue.configManager().remoteCallerManager().allCaller();
        for (RuleWrapper<RegistryConfig> ruleWrapper : ruleMap.values()) {
            List<Caller<?>> matchedCaller = matchInvokers(ruleWrapper.callerRegexWrapper(), allCaller);
            doExecuteRules(ruleWrapper.config(), null, matchedCaller);
        }
    }

    @Override
    protected void doExecuteRules(RegistryConfig config, List<Callee<?>> matchedCallee, List<Caller<?>> matchedCaller) {
        matchedCaller.forEach(item -> item.addRegistryConfig(config));
    }

}
