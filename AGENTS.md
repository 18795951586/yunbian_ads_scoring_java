# 云辨投流 Python→Java 迁移项目规则

## 工作方式
- 继续同一个项目，不重讲背景，不回到 openclaw 线，不重新做路线规划
- 你是本项目里的 Codex / Qoder / Claude Code 总工程师助手
- 一次只推进一步：先验收，再给下一步
- 默认先做只读验收，不要跳到大重构
- 不要被工具带节奏，要主导节奏
- 如果我给的是 summary，不够就要求我贴真实代码或真实输出
- 如果改代码，优先给整文件替换版
- Git 收口前，先确认 working tree 边界清楚
- .env 不进 Git

## 当前实现方向
- 这是“云辨投流 Python→Java 迁移”项目
- Java 评分层不直译旧 Python 评分逻辑，直接按新规则走
- 所有比较必须发生在同评分方案下

## 可比池规则
- 所有评分必须先进入可比池，不能跨不可比对象乱比
- 至少同店铺
- campaign：同评分方案
- adgroup：同评分方案 + 同 campaign
- bidword：同评分方案 + 同 campaign + 同 adgroup

## 固定 7 指标
- roi：正向
- cvr：正向
- cpc：反向，底层字段映射 ecpc
- cart_cost：反向
- deal_new_customer_ratio：正向
- new_customer_ratio：正向
- direct_deal_ratio：正向

## 三个基础规则母版
- ranking
- target_value（单目标值）
- smart_benchmark

## ranking 规则
- 在同比较池内排名
- 正向高者优，反向低者优
- 并列按标准竞争排名：1,1,3
- score = (N - rank) / (N - 1) × 100
- 只有 1 个实体时直接 100

## target_value 规则
- 用户只填 1 个目标值
- 正向：当前值 >= 目标值 -> 100
- 正向：当前值 < 目标值 -> (当前值 / 目标值) × 100
- 反向：当前值 <= 目标值 -> 100
- 反向：当前值 > 目标值 -> (目标值 / 当前值) × 100
- 最终限制在 [0,100]

## smart_benchmark 规则
- 正向指标：benchmarkValue = 池内最大值
- 反向指标：benchmarkValue = 池内最小值
- 正向得分 = 当前值 / benchmarkValue × 100
- 反向得分 = benchmarkValue / 当前值 × 100
- 最终限制在 [0,100]
- 不允许混入 ranking 分，不允许先排位再算 benchmark 分

## 综合评分
- 只统计 enabled = true 的指标
- 各指标先独立算 score
- weightedScore = score × weight
- totalScore = weightedScoreSum / participatingWeightSum
- 如果某个实体在某个 enabled metric 上没有 metricValue，则该 metric 不参与这个实体的加权
- participatingWeightSum 只统计该实体实际参与计算的指标权重
- totalScore 保留 4 位小数

## 实现与验收约束
- 后续所有 preview / calculate 实现，必须严格服从以上口径
- 验收时不能只看接口通不通，必须核对分池、方向、公式、缺失值跳过、participatingWeightSum 口径、totalScore 保留 4 位小数
- 如果只是小语义问题但不影响当前主线，不要硬开新刀
- 如果 impl 文件越来越大，优先考虑拆分类，而不是继续堆逻辑
