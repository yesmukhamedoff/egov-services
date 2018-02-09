package org.egov.swm.persistence.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.swm.domain.model.Asset;
import org.egov.swm.domain.model.BinDetails;
import org.egov.swm.domain.model.BinDetailsSearch;
import org.egov.swm.domain.model.Boundary;
import org.egov.swm.domain.model.CollectionPoint;
import org.egov.swm.domain.model.CollectionPointDetails;
import org.egov.swm.domain.model.CollectionPointDetailsSearch;
import org.egov.swm.domain.model.CollectionPointSearch;
import org.egov.swm.domain.model.CollectionType;
import org.egov.swm.domain.model.Pagination;
import org.egov.swm.domain.service.AssetService;
import org.egov.swm.domain.service.BoundaryService;
import org.egov.swm.domain.service.CollectionTypeService;
import org.egov.swm.persistence.entity.CollectionPointEntity;
import org.egov.swm.web.controller.CollectionPointController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

@Service
public class CollectionPointJdbcRepository extends JdbcRepository {

    public static final String TABLE_NAME = "egswm_collectionpoint";

    private static final Logger LOG = LoggerFactory.getLogger(CollectionPointJdbcRepository.class);


    @Autowired
    public BinDetailsJdbcRepository binIdDetailsJdbcRepository;

    @Autowired
    public CollectionPointDetailsJdbcRepository collectionPointDetailsJdbcRepository;

    @Autowired
    private CollectionTypeService collectionTypeService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private BoundaryService boundaryService;

    public Boolean uniqueCheck(final String tenantId, final String fieldName, final String fieldValue,
            final String uniqueFieldName,
            final String uniqueFieldValue) {

        return uniqueCheck(TABLE_NAME, tenantId, fieldName, fieldValue, uniqueFieldName, uniqueFieldValue);
    }

    public Pagination<CollectionPoint> search(final CollectionPointSearch searchRequest) {

        long start = System.currentTimeMillis();
        String searchQuery = "select * from " + TABLE_NAME + " :condition  :orderby ";

        long end = System.currentTimeMillis();
        LOG.info("Time taken for searchQuery Concat in CPJDBCREPO " + (end - start) + "ms");
        final Map<String, Object> paramValues = new HashMap<>();
        final StringBuffer params = new StringBuffer();

        if (searchRequest.getSortBy() != null && !searchRequest.getSortBy().isEmpty()) {
            validateSortByOrder(searchRequest.getSortBy());
            validateEntityFieldName(searchRequest.getSortBy(), CollectionPointSearch.class);
        }

        String orderBy = "order by name";
        if (searchRequest.getSortBy() != null && !searchRequest.getSortBy().isEmpty())
            orderBy = "order by " + searchRequest.getSortBy();

        start = System.currentTimeMillis();
        if (searchRequest.getCodes() != null) {
            addAnd(params);
            params.append("code in (:codes)");
            paramValues.put("codes", new ArrayList<>(Arrays.asList(searchRequest.getCodes().split(","))));
        }
        end = System.currentTimeMillis();
        LOG.info("Time taken Cheacking if condition in CPJDBCREPO " + (end - start) + "ms");


        start = System.currentTimeMillis();
        if (searchRequest.getTenantId() != null) {
            addAnd(params);
            params.append("tenantId =:tenantId");
            paramValues.put("tenantId", searchRequest.getTenantId());
        }
        end = System.currentTimeMillis();
        LOG.info("Time taken adding tenantID condition in CPJDBCREPO " + (end - start) + "ms");

        if (searchRequest.getCode() != null) {
            addAnd(params);
            params.append("code =:code");
            paramValues.put("code", searchRequest.getCode());
        }

        if (searchRequest.getName() != null) {
            addAnd(params);
            params.append("name =:name");
            paramValues.put("name", searchRequest.getName());
        }

        if (searchRequest.getLocationCode() != null) {
            addAnd(params);
            params.append("location =:location");
            paramValues.put("location", searchRequest.getLocationCode());
        }

        Pagination<CollectionPoint> page = new Pagination<>();
        if (searchRequest.getOffset() != null)
            page.setOffset(searchRequest.getOffset());
        if (searchRequest.getPageSize() != null)
            page.setPageSize(searchRequest.getPageSize());

        start = System.currentTimeMillis();
        if (params.length() > 0)
            searchQuery = searchQuery.replace(":condition", " where " + params.toString());
        else

            searchQuery = searchQuery.replace(":condition", "");


        searchQuery = searchQuery.replace(":orderby", orderBy);

        end = System.currentTimeMillis();
        LOG.info("Time taken for replacing conditions in search query " + (end - start) + "ms");


        start = System.currentTimeMillis();

        page = (Pagination<CollectionPoint>) getPagination(searchQuery, page, paramValues);


        searchQuery = searchQuery + " :pagination";

        searchQuery = searchQuery.replace(":pagination",
                "limit " + page.getPageSize() + " offset " + page.getOffset() * page.getPageSize());

        end = System.currentTimeMillis();
        LOG.info("Time taken for pagination " + (end - start) + "ms");

        final BeanPropertyRowMapper row = new BeanPropertyRowMapper(CollectionPointEntity.class);

        final List<CollectionPoint> collectionPointList = new ArrayList<>();

        start = System.currentTimeMillis();

        final List<CollectionPointEntity> collectionPointEntities = namedParameterJdbcTemplate.query(searchQuery.toString(),
                paramValues, row);

        LOG.info("Search Query CollectionPointEntity " + searchQuery.toString());

        end = System.currentTimeMillis();
        LOG.info("Time taken for returning data from db " + (end - start) + "ms");

        StringBuffer collectionPointCodes = new StringBuffer();

        start = System.currentTimeMillis();
        for (final CollectionPointEntity collectionPointEntity : collectionPointEntities) {
            collectionPointList.add(collectionPointEntity.toDomain());

            if (collectionPointCodes.length() >= 1)
                collectionPointCodes.append(",");

            collectionPointCodes.append(collectionPointEntity.getCode());
        }

        end = System.currentTimeMillis();
        LOG.info("Time taken for mapping entity to domain " + (end - start) + "ms");


        start = System.currentTimeMillis();
        if (collectionPointList != null && !collectionPointList.isEmpty()) {

            start = System.currentTimeMillis();
                populateBoundarys(collectionPointList);
            end = System.currentTimeMillis();
            LOG.info("Time taken for populating Boundary MDMS " + (end - start) + "ms");

            start = System.currentTimeMillis();
                populateBinDetails(collectionPointList, collectionPointCodes.toString());

            end = System.currentTimeMillis();
            LOG.info("Time taken for populating Bin Details " + (end - start) + "ms");

            start = System.currentTimeMillis();
                populateCollectionPointDetails(collectionPointList, collectionPointCodes.toString());
            end = System.currentTimeMillis();
            LOG.info("Time taken for populating populateCollectionPointDetails " + (end - start) + "ms");

        }

        end = System.currentTimeMillis();
        LOG.info("Time taken for populating data " + (end - start) + "ms");

        page.setPagedData(collectionPointList);

        return page;
    }

    private void populateBoundarys(List<CollectionPoint> collectionPointList) {

        String tenantId = null;
        Map<String, Boundary> boundaryMap = new HashMap<>();

        if (collectionPointList != null && !collectionPointList.isEmpty())
            tenantId = collectionPointList.get(0).getTenantId();

        List<Boundary> boundarys = boundaryService.getAll(tenantId, new RequestInfo());

        if (boundarys != null) {

            for (Boundary bd : boundarys) {

                boundaryMap.put(bd.getCode(), bd);

            }
        }

        for (CollectionPoint collectionPoint : collectionPointList) {

            if (collectionPoint.getLocation() != null && collectionPoint.getLocation().getCode() != null
                    && !collectionPoint.getLocation().getCode().isEmpty()) {

                collectionPoint.setLocation(boundaryMap.get(collectionPoint.getLocation().getCode()));
            }

        }

    }

    private void populateBinDetails(List<CollectionPoint> collectionPointList, String collectionPointCodes) {
        Map<String, List<BinDetails>> binDetailsMap = new HashMap<>();
        Map<String, Asset> assetMap = new HashMap<>();
        String tenantId = null;
        BinDetailsSearch bds;
        bds = new BinDetailsSearch();

        if (collectionPointList != null && !collectionPointList.isEmpty())
            tenantId = collectionPointList.get(0).getTenantId();

        bds.setCollectionPoints(collectionPointCodes);
        bds.setTenantId(tenantId);

        List<BinDetails> binDetails = binIdDetailsJdbcRepository.search(bds);

        List<BinDetails> bdList;

        for (BinDetails bd : binDetails) {

            if (binDetailsMap.get(bd.getCollectionPoint()) == null) {

                binDetailsMap.put(bd.getCollectionPoint(), Collections.singletonList(bd));

            } else {

                bdList = new ArrayList<>(binDetailsMap.get(bd.getCollectionPoint()));

                bdList.add(bd);

                binDetailsMap.put(bd.getCollectionPoint(), bdList);

            }
        }

        List<Asset> assets = assetService.getAll(tenantId, new RequestInfo());

        if (assets != null)
            for (Asset asset : assets) {
                assetMap.put(asset.getCode(), asset);
            }

        for (CollectionPoint collectionPoint : collectionPointList) {

            collectionPoint.setBinDetails(binDetailsMap.get(collectionPoint.getCode()));

        }

        for (CollectionPoint collectionPoint : collectionPointList) {

            if (collectionPoint.getBinDetails() != null)
                for (BinDetails bd : collectionPoint.getBinDetails()) {

                    if (bd.getAsset() != null && bd.getAsset().getCode() != null)
                        bd.setAsset(assetMap.get(bd.getAsset().getCode()));
                }

        }

    }

    private void populateCollectionPointDetails(List<CollectionPoint> collectionPointList, String collectionPointCodes) {
        Map<String, List<CollectionPointDetails>> collectionPointDetailsMap = new HashMap<>();
        Map<String, CollectionType> collectionTypeMap = new HashMap<>();
        String tenantId = null;
        CollectionPointDetailsSearch cpds;
        cpds = new CollectionPointDetailsSearch();

        if (collectionPointList != null && !collectionPointList.isEmpty())
            tenantId = collectionPointList.get(0).getTenantId();

        cpds.setCollectionPoints(collectionPointCodes);
        cpds.setTenantId(tenantId);

        List<CollectionPointDetails> collectionPointDetails = collectionPointDetailsJdbcRepository.search(cpds);

        long start = System.currentTimeMillis();
            List<CollectionType> collectionTypes = collectionTypeService.getAll(tenantId, new RequestInfo());
        long end = System.currentTimeMillis();
        LOG.info("Time taken for populating collectionTypeService MDMS " + (end - start) + "ms");

        for (CollectionType ct : collectionTypes) {
            collectionTypeMap.put(ct.getCode(), ct);
        }
        List<CollectionPointDetails> cpdList;
        for (CollectionPointDetails cpd : collectionPointDetails) {

            if (cpd.getCollectionType() != null && cpd.getCollectionType().getCode() != null
                    && !cpd.getCollectionType().getCode().isEmpty()) {

                cpd.setCollectionType(collectionTypeMap.get(cpd.getCollectionType().getCode()));
            }

            if (collectionPointDetailsMap.get(cpd.getCollectionPoint()) == null) {

                collectionPointDetailsMap.put(cpd.getCollectionPoint(), Collections.singletonList(cpd));

            } else {

                cpdList = new ArrayList<>(collectionPointDetailsMap.get(cpd.getCollectionPoint()));

                cpdList.add(cpd);

                collectionPointDetailsMap.put(cpd.getCollectionPoint(), cpdList);

            }
        }

        for (CollectionPoint collectionPoint : collectionPointList) {

            collectionPoint.setCollectionPointDetails(collectionPointDetailsMap.get(collectionPoint.getCode()));

        }

    }

}